import {
  Client,
  IMessage,
  ReconnectionTimeMode,
  StompSubscription,
} from '@stomp/stompjs';
import SockJS from 'sockjs-client';

import { getCsrfToken, isServer } from '@/util/server';

import { env } from './env';
import { primeCsrfToken } from './server';

export type StompMessageCallback = (message: IMessage) => void;

export interface SubscribeOptions {
  /**
   * 실제 STOMP 구독이 (재)성립될 때마다 호출된다. 최초 연결과 매 재연결에서
   * 모두 불린다. 끊겨 있던 동안 놓친 메시지를 다시 받아오는 용도.
   */
  onSubscribed?: () => void;
}

interface DestinationEntry {
  listeners: Set<StompMessageCallback>;
  onSubscribedHandlers: Set<() => void>;
  /** 현재 STOMP 연결에서 성립한 실제 구독. 연결이 끊기면 null로 되돌린다. */
  subscription: StompSubscription | null;
}

/**
 * 마지막 구독자가 사라진 뒤 연결을 끊기까지의 유예. 라우트 전환에서
 * 이전 화면의 unmount와 다음 화면의 mount 사이 공백 때문에 끊었다 다시
 * 붙는 것을 막는다.
 */
const IDLE_DISCONNECT_DELAY_MS = 5000;

let client: Client | null = null;
let idleTimer: ReturnType<typeof setTimeout> | null = null;

/** 목적지별 "구독하고자 하는 상태". 연결 수명과 무관하게 유지된다. */
const destinations = new Map<string, DestinationEntry>();

function debugLog(message: string) {
  if (process.env.NODE_ENV !== 'production') {
    // eslint-disable-next-line no-console -- intentional STOMP protocol debug logging, pre-existing
    console.log(`[STOMP] ${message}`);
  }
}

/**
 * 연결이 성립할 때마다 호출된다. stompjs는 재연결 시 StompHandler를 새로
 * 만들고 그 안의 구독 맵도 새로 만들므로(이전 구독은 복원되지 않는다),
 * 여기서 원하는 구독을 전부 다시 건다.
 */
function openPendingSubscriptions(stompClient: Client) {
  for (const [destination, entry] of destinations) {
    if (entry.subscription || entry.listeners.size === 0) {
      continue;
    }

    entry.subscription = stompClient.subscribe(destination, (message) => {
      // 디스패치 도중 리스너가 해지되어도 순회가 깨지지 않도록 복사본을 돈다.
      for (const listener of [...entry.listeners]) {
        listener(message);
      }
    });

    for (const handler of [...entry.onSubscribedHandlers]) {
      handler();
    }
  }
}

/** 소켓이 닫히면 기존 구독 핸들은 모두 죽은 것이므로 버린다(원하는 상태는 유지). */
function dropSubscriptionHandles() {
  for (const entry of destinations.values()) {
    entry.subscription = null;
  }
}

function getStompClient(): Client {
  if (isServer()) {
    throw new Error('getStompClient can only be called in the browser');
  }

  if (client) {
    return client;
  }

  const url = env.NEXT_PUBLIC_BACKEND_URL + '/ws';

  // stompjs의 activate()는 DEACTIVATING 중이면 deactivate().then(재activate)를
  // 큐잉하는데, 이 continuation은 인스턴스에 묶여 있어 resetStompConnection이
  // 취소할 수 없다. reset 뒤 그 continuation이 버려진 인스턴스를 되살리면
  // 익명 상태의 영구 재연결 루프가 된다. 인스턴스를 캡처해 두고, 현행
  // 클라이언트가 아니게 된 순간 스스로 종료시켜 부활을 차단한다.
  const instance: Client = new Client({
    webSocketFactory: () => {
      return new SockJS(url);
    },
    // 서버가 STOMP CONNECT 프레임에서 CSRF 토큰을 검증한다(HTTP 요청과 동일한
    // X-XSRF-TOKEN 헤더). 토큰이 회전될 수 있으므로 재연결마다 새로 읽는다.
    // 첫 방문이라 아직 쿠키가 없으면 여기서 직접 발급받는다. QueryProvider의
    // primeCsrfToken()은 await되지 않아 CONNECT보다 늦을 수 있다.
    beforeConnect: async (stompClient) => {
      if (instance !== client) {
        // 고아 인스턴스 — 소켓을 열기 전에 끊는다. deactivate로 상태가
        // 내려가면 stompjs의 _connect가 소켓 생성 전에 중단한다.
        void instance.deactivate();
        return;
      }

      let csrfToken = await getCsrfToken();

      if (!csrfToken) {
        await primeCsrfToken();
        csrfToken = await getCsrfToken();
      }

      // 이전 연결의 만료된 토큰이 남지 않도록 항상 통째로 덮어쓴다.
      stompClient.connectHeaders = csrfToken
        ? { 'X-XSRF-TOKEN': csrfToken }
        : {};
    },
    onConnect: () => {
      if (instance !== client) {
        void instance.deactivate();
        return;
      }

      openPendingSubscriptions(instance);
    },
    onWebSocketClose: () => {
      dropSubscriptionHandles();
    },
    onStompError: (frame) => {
      debugLog(`broker error: ${frame.headers['message'] ?? ''}`);
    },
    // 프레임에 알림 payload가 그대로 담기므로 프로덕션 콘솔에는 남기지 않는다.
    debug: debugLog,
    reconnectDelay: 5000,
    // 세션 만료·인가 거부처럼 영구적인 실패에서 5초마다 무한히 두드리지 않도록
    // 백오프를 건다.
    reconnectTimeMode: ReconnectionTimeMode.EXPONENTIAL,
    maxReconnectDelay: 60_000,
    heartbeatIncoming: 4000,
    heartbeatOutgoing: 4000,
  });

  client = instance;
  return instance;
}

/**
 * 원하는 구독이 하나라도 있으면 연결하고, 하나도 없으면 유예 뒤 끊는다.
 * 익명 사용자는 구독하지 않으므로 연결 자체가 시작되지 않는다.
 */
function syncActivation() {
  if (destinations.size > 0) {
    if (idleTimer) {
      clearTimeout(idleTimer);
      idleTimer = null;
    }

    // 이미 ACTIVE면 stompjs가 무시하고, DEACTIVATING이면 종료를 기다렸다 켠다.
    getStompClient().activate();
    return;
  }

  if (idleTimer) {
    return;
  }

  idleTimer = setTimeout(() => {
    idleTimer = null;

    if (destinations.size === 0 && client) {
      void client.deactivate();
    }
  }, IDLE_DISCONNECT_DELAY_MS);
}

/**
 * 목적지 구독을 "예약"한다. 아직 연결 전이면 연결 직후에, 이미 연결돼
 * 있으면 즉시 실제 구독이 성립한다. 재연결마다 자동으로 다시 걸린다.
 *
 * 같은 목적지에 여러 소비자가 붙어도 실제 STOMP 구독은 하나만 만들고
 * 콜백을 팬아웃한다.
 */
export function subscribeTo(
  destination: string,
  callback: StompMessageCallback,
  options?: SubscribeOptions,
): StompSubscription {
  const stompClient = getStompClient();

  const existing = destinations.get(destination);
  const entry: DestinationEntry = existing ?? {
    listeners: new Set<StompMessageCallback>(),
    onSubscribedHandlers: new Set<() => void>(),
    subscription: null,
  };

  if (!existing) {
    destinations.set(destination, entry);
  }

  entry.listeners.add(callback);

  const onSubscribed = options?.onSubscribed;
  if (onSubscribed) {
    entry.onSubscribedHandlers.add(onSubscribed);
  }

  syncActivation();

  // active 검사가 없으면 graceful deactivate(DISCONNECT 전송 후 close 대기)
  // 창에서 connected가 아직 true라, 죽어가는 연결에 SUBSCRIBE를 보내고
  // onSubscribed를 헛되이 발화한다. DEACTIVATING이면 예약만 남기고 재연결의
  // onConnect에 맡긴다.
  if (stompClient.connected && stompClient.active && !entry.subscription) {
    openPendingSubscriptions(stompClient);
  } else if (stompClient.connected && stompClient.active && onSubscribed) {
    // 이미 남이 구독을 열어둔 목적지에 뒤늦게 붙은 경우.
    onSubscribed();
  }

  let released = false;

  return {
    get id() {
      return entry.subscription?.id ?? `pending-${destination}`;
    },
    unsubscribe() {
      if (released) {
        return;
      }
      released = true;

      entry.listeners.delete(callback);
      if (onSubscribed) {
        entry.onSubscribedHandlers.delete(onSubscribed);
      }

      if (entry.listeners.size > 0) {
        return;
      }

      try {
        entry.subscription?.unsubscribe();
      } catch {
        // 소켓이 이미 닫혔으면 UNSUBSCRIBE 프레임을 보낼 수 없다. 어차피
        // 브로커 쪽 구독도 연결과 함께 사라졌으므로 무시해도 된다.
      }
      entry.subscription = null;

      // reset 이후 같은 목적지로 새 엔트리가 만들어졌을 수 있다 — 레지스트리가
      // 여전히 내 엔트리를 가리킬 때만 지운다. 남의 엔트리를 지우면 살아 있는
      // 소비자의 구독 의사가 유실된다.
      if (destinations.get(destination) === entry) {
        destinations.delete(destination);
        syncActivation();
      }
    },
  };
}

/**
 * 연결돼 있으면 전송하고 true, 아니면 아무것도 하지 않고 false를 반환한다.
 *
 * 주의: 연결은 lazy activation(활성 구독 존재)으로만 시작되므로, 구독 없이
 * 발행만 하는 소비자는 항상 false를 받는다. 그런 소비자가 필요해지면 발행
 * 측에서도 연결을 붙잡아 두는 명시적 retain API를 먼저 추가해야 한다.
 */
export function publishTo(destination: string, body: string): boolean {
  if (!client?.connected) {
    return false;
  }

  client.publish({ destination, body });
  return true;
}

/**
 * 로그아웃처럼 principal이 바뀌는 시점에 연결을 즉시 버린다.
 *
 * idle 유예(5초)에 맡기면 그 사이에 다른 계정으로 로그인했을 때 이전 Spring
 * 세션에 묶인 소켓이 그대로 재사용된다 — 서버의 STOMP Principal은 핸드셰이크
 * 시점에 고정되므로 재연결 없이는 절대 갱신되지 않는다.
 */
export function resetStompConnection(): void {
  if (idleTimer) {
    clearTimeout(idleTimer);
    idleTimer = null;
  }

  // 살아 있는 핸들이 뒤늦게 unsubscribe()를 불러도 죽은 소켓에 프레임을
  // 보내지 않도록, 구독 핸들부터 무효화한 뒤 원하는-상태 맵을 비운다.
  dropSubscriptionHandles();
  destinations.clear();

  if (client) {
    void client.deactivate();
    client = null;
  }
}
