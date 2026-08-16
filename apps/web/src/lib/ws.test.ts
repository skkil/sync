import { afterEach, beforeEach, describe, expect, it, vi } from 'vitest';

/**
 * `@stomp/stompjs`의 Client를 통째로 대체하는 가짜. 생성자 config로 받은
 * beforeConnect/onConnect/onWebSocketClose 콜백을 테스트가 직접 발화시켜
 * 연결 수립·단절을 시뮬레이션한다.
 */
const h = vi.hoisted(() => {
  type MessageHandler = (message: { body: string }) => void;

  class FakeClient {
    // eslint-disable-next-line @typescript-eslint/no-explicit-any -- 테스트 더블: stompjs Client config를 느슨하게 받는다
    config: Record<string, any>;
    connected = false;
    active = false;
    /** graceful deactivate 창(DISCONNECT 전송 후 close 대기)을 시뮬레이션한다. */
    deactivating = false;
    connectHeaders: Record<string, string> = {};
    activateCalls = 0;
    deactivateCalls = 0;
    subscribeCalls: string[] = [];
    unsubscribeCalls = 0;
    private handlers = new Map<string, MessageHandler>();

    // eslint-disable-next-line @typescript-eslint/no-explicit-any -- 테스트 더블
    constructor(config: Record<string, any>) {
      this.config = config;
      clients.push(this);
    }

    activate() {
      this.activateCalls += 1;
      // 실제 stompjs는 DEACTIVATING 중의 activate()를 큐잉만 하고 상태를
      // 바로 ACTIVE로 만들지 않는다.
      if (!this.deactivating) {
        this.active = true;
      }
    }

    async deactivate() {
      this.deactivateCalls += 1;
      this.active = false;
      this.connected = false;
    }

    subscribe(destination: string, callback: MessageHandler) {
      this.subscribeCalls.push(destination);
      this.handlers.set(destination, callback);
      return {
        id: `sub-${this.subscribeCalls.length}`,
        unsubscribe: () => {
          this.unsubscribeCalls += 1;
          this.handlers.delete(destination);
        },
      };
    }

    publish() {}

    /** 핸드셰이크 완료를 시뮬레이션한다 (beforeConnect → CONNECTED). */
    async connect() {
      await this.config.beforeConnect?.(this);
      this.connected = true;
      this.config.onConnect?.();
    }

    /** 소켓 단절을 시뮬레이션한다. */
    close() {
      this.connected = false;
      this.handlers.clear();
      this.config.onWebSocketClose?.();
    }

    /** 브로커가 MESSAGE 프레임을 보낸 상황을 시뮬레이션한다. */
    emit(destination: string, body: string) {
      this.handlers.get(destination)?.({ body });
    }
  }

  const clients: FakeClient[] = [];
  return { FakeClient, clients };
});

vi.mock('@stomp/stompjs', () => ({
  Client: h.FakeClient,
  ReconnectionTimeMode: { LINEAR: 'LINEAR', EXPONENTIAL: 'EXPONENTIAL' },
}));
vi.mock('sockjs-client', () => ({ default: vi.fn() }));
vi.mock('@/lib/env', () => ({
  env: { NEXT_PUBLIC_BACKEND_URL: 'http://localhost:8080' },
}));
vi.mock('@/util/server', () => ({
  isServer: () => false,
  getCsrfToken: vi.fn(async () => 'csrf-token'),
}));
vi.mock('@/lib/server', () => ({
  primeCsrfToken: vi.fn(async () => undefined),
}));

/** 모듈 상태(레지스트리·싱글턴)를 매 테스트 격리해서 다시 불러온다. */
async function loadWs() {
  vi.resetModules();
  h.clients.length = 0;

  const ws = await import('@/lib/ws');
  const utilServer = await import('@/util/server');
  const libServer = await import('@/lib/server');

  return {
    ws,
    getCsrfToken: vi.mocked(utilServer.getCsrfToken),
    primeCsrfToken: vi.mocked(libServer.primeCsrfToken),
  };
}

/** noUncheckedIndexedAccess 아래에서 undefined를 배제하고 꺼낸다. */
function clientAt(index = 0) {
  const instance = h.clients[index];
  if (!instance) {
    throw new Error(`FakeClient ${index}번이 생성되지 않았다`);
  }
  return instance;
}

const DESTINATION = '/user/queue/notifications';

beforeEach(() => {
  vi.useFakeTimers();
});

afterEach(() => {
  vi.useRealTimers();
});

describe('구독 예약과 연결 수명', () => {
  it('연결 전에 구독해도 실패하지 않고, 연결 직후 실제 구독이 성립한다', async () => {
    const { ws } = await loadWs();
    const received: string[] = [];

    const handle = ws.subscribeTo(DESTINATION, (message) =>
      received.push(message.body),
    );

    // 구독 레이스 회귀: 연결 전 호출이 오류가 아니라 예약이어야 한다.
    expect(handle).toBeDefined();
    const client = clientAt();
    expect(client.activateCalls).toBe(1);
    expect(client.subscribeCalls).toHaveLength(0);

    await client.connect();

    expect(client.subscribeCalls).toEqual([DESTINATION]);
    client.emit(DESTINATION, '{"id":1}');
    expect(received).toEqual(['{"id":1}']);
  });

  it('재연결하면 구독이 자동 복원되고 onSubscribed가 다시 불린다', async () => {
    const { ws } = await loadWs();
    const received: string[] = [];
    const onSubscribed = vi.fn();

    ws.subscribeTo(DESTINATION, (message) => received.push(message.body), {
      onSubscribed,
    });
    const client = clientAt();
    await client.connect();
    expect(onSubscribed).toHaveBeenCalledTimes(1);

    // stompjs는 재연결 시 구독을 복원하지 않는다 — 레지스트리가 다시 걸어야 한다.
    client.close();
    await client.connect();

    expect(client.subscribeCalls).toEqual([DESTINATION, DESTINATION]);
    expect(onSubscribed).toHaveBeenCalledTimes(2);
    client.emit(DESTINATION, '{"id":2}');
    expect(received).toEqual(['{"id":2}']);
  });

  it('같은 목적지의 두 소비자는 STOMP 구독 하나를 공유한다', async () => {
    const { ws } = await loadWs();
    const first: string[] = [];
    const second: string[] = [];

    const handleA = ws.subscribeTo(DESTINATION, (m) => first.push(m.body));
    const handleB = ws.subscribeTo(DESTINATION, (m) => second.push(m.body));
    const client = clientAt();
    await client.connect();

    expect(client.subscribeCalls).toEqual([DESTINATION]);
    client.emit(DESTINATION, '{"id":3}');
    expect(first).toEqual(['{"id":3}']);
    expect(second).toEqual(['{"id":3}']);

    // 한 명이 떠나도 남은 소비자의 구독은 유지된다.
    handleA.unsubscribe();
    expect(client.unsubscribeCalls).toBe(0);
    client.emit(DESTINATION, '{"id":4}');
    expect(first).toEqual(['{"id":3}']);
    expect(second).toEqual(['{"id":3}', '{"id":4}']);

    // 마지막 소비자가 떠나면 실제 UNSUBSCRIBE가 나간다.
    handleB.unsubscribe();
    expect(client.unsubscribeCalls).toBe(1);
  });

  it('같은 핸들을 두 번 해지해도 다른 소비자에게 영향이 없다', async () => {
    const { ws } = await loadWs();
    const received: string[] = [];

    const handleA = ws.subscribeTo(DESTINATION, () => {});
    ws.subscribeTo(DESTINATION, (m) => received.push(m.body));
    const client = clientAt();
    await client.connect();

    handleA.unsubscribe();
    handleA.unsubscribe();

    client.emit(DESTINATION, '{"id":5}');
    expect(received).toEqual(['{"id":5}']);
  });

  it('graceful deactivate 창에서는 즉시 구독하지 않고 예약만 남긴다', async () => {
    const { ws } = await loadWs();
    const onSubscribed = vi.fn();

    ws.subscribeTo(DESTINATION, () => {});
    const client = clientAt();
    await client.connect();

    // DISCONNECT는 보냈지만 소켓 close 전 — connected는 아직 true다.
    client.deactivating = true;
    client.active = false;

    const subscribesBefore = client.subscribeCalls.length;
    ws.subscribeTo(DESTINATION, () => {}, { onSubscribed });
    ws.subscribeTo('/user/queue/other', () => {});

    // 죽어가는 연결에 SUBSCRIBE를 보내거나 onSubscribed를 헛발화하면 안 된다.
    expect(client.subscribeCalls.length).toBe(subscribesBefore);
    expect(onSubscribed).not.toHaveBeenCalled();
  });
});

describe('lazy activation', () => {
  it('구독이 없으면 클라이언트도 연결도 만들지 않는다', async () => {
    await loadWs();

    // 모듈 import만으로는 아무 일도 일어나지 않아야 한다 — 익명 방문자가
    // 5초마다 401 핸드셰이크를 반복하던 결함의 회귀 테스트.
    expect(h.clients).toHaveLength(0);
  });

  it('마지막 구독자가 떠나면 유예 후 연결을 끊는다', async () => {
    const { ws } = await loadWs();

    const handle = ws.subscribeTo(DESTINATION, () => {});
    const client = clientAt();
    await client.connect();

    handle.unsubscribe();
    expect(client.deactivateCalls).toBe(0);

    vi.advanceTimersByTime(5000);
    expect(client.deactivateCalls).toBe(1);
  });

  it('유예 안에 새 구독이 생기면 연결을 유지한다', async () => {
    const { ws } = await loadWs();

    const handle = ws.subscribeTo(DESTINATION, () => {});
    const client = clientAt();
    await client.connect();

    // 라우트 전환: 이전 화면 unmount 직후 다음 화면이 다시 구독한다.
    handle.unsubscribe();
    ws.subscribeTo(DESTINATION, () => {});

    vi.advanceTimersByTime(5000);
    expect(client.deactivateCalls).toBe(0);
  });
});

describe('resetStompConnection', () => {
  it('유예 없이 즉시 연결을 버리고, 이후 구독은 새 클라이언트로 시작한다', async () => {
    const { ws } = await loadWs();

    const handle = ws.subscribeTo(DESTINATION, () => {});
    const client = clientAt();
    await client.connect();

    // 로그아웃: idle 유예를 기다리면 계정 전환 시 이전 세션 소켓이 재사용된다.
    ws.resetStompConnection();
    expect(client.deactivateCalls).toBe(1);

    // 죽은 핸들의 뒤늦은 해지가 죽은 소켓에 프레임을 보내지 않는다.
    expect(() => handle.unsubscribe()).not.toThrow();
    expect(client.unsubscribeCalls).toBe(0);

    // 재로그인: 새 Principal은 새 소켓으로 연결돼야 한다.
    ws.subscribeTo(DESTINATION, () => {});
    expect(h.clients).toHaveLength(2);
    expect(clientAt(1).activateCalls).toBe(1);
  });

  it('reset 이후 옛 인스턴스의 지연된 콜백은 스스로 종료하고 부활하지 못한다', async () => {
    const { ws } = await loadWs();

    ws.subscribeTo(DESTINATION, () => {});
    const orphan = clientAt(0);
    await orphan.connect();

    ws.resetStompConnection();
    ws.subscribeTo(DESTINATION, () => {});
    const current = clientAt(1);

    // stompjs가 인스턴스 내부에 큐잉해 둔 activate continuation이 reset 뒤
    // 뒤늦게 재개된 상황 — 고아가 재연결 루프를 시작하면 안 된다.
    const deactivatesBefore = orphan.deactivateCalls;
    await orphan.config.beforeConnect?.(orphan);
    orphan.config.onConnect?.();

    expect(orphan.deactivateCalls).toBeGreaterThan(deactivatesBefore);
    // 고아의 onConnect가 미연결 상태인 새 클라이언트를 건드리지 않는다.
    expect(current.subscribeCalls).toHaveLength(0);
  });

  it('reset 이전 핸들의 뒤늦은 해지가 새 구독 의사를 지우지 않는다', async () => {
    const { ws } = await loadWs();
    const received: string[] = [];

    const staleHandle = ws.subscribeTo(DESTINATION, () => {});
    await clientAt(0).connect();

    ws.resetStompConnection();
    ws.subscribeTo(DESTINATION, (m) => received.push(m.body));
    staleHandle.unsubscribe();

    const current = clientAt(1);
    await current.connect();

    expect(current.subscribeCalls).toEqual([DESTINATION]);
    current.emit(DESTINATION, '{"id":9}');
    expect(received).toEqual(['{"id":9}']);
  });
});

describe('STOMP CONNECT CSRF', () => {
  it('쿠키의 토큰을 X-XSRF-TOKEN 헤더로 싣는다', async () => {
    const { ws, getCsrfToken } = await loadWs();
    getCsrfToken.mockResolvedValue('token-a');

    ws.subscribeTo(DESTINATION, () => {});
    const client = clientAt();
    await client.connect();

    expect(client.connectHeaders).toEqual({ 'X-XSRF-TOKEN': 'token-a' });
  });

  it('쿠키가 없으면 프라이밍 후 다시 읽는다', async () => {
    const { ws, getCsrfToken, primeCsrfToken } = await loadWs();
    // 첫 방문: QueryProvider의 primeCsrfToken()이 CONNECT보다 늦는 레이스.
    getCsrfToken
      .mockResolvedValueOnce(undefined)
      .mockResolvedValueOnce('fresh-token');

    ws.subscribeTo(DESTINATION, () => {});
    const client = clientAt();
    await client.connect();

    expect(primeCsrfToken).toHaveBeenCalledTimes(1);
    expect(client.connectHeaders).toEqual({ 'X-XSRF-TOKEN': 'fresh-token' });
  });
});

describe('publishTo', () => {
  it('연결돼 있으면 true, 아니면 아무것도 하지 않고 false를 반환한다', async () => {
    const { ws } = await loadWs();

    // lazy activation 계약: 구독이 없으면 연결이 없으므로 발행은 항상 실패한다.
    expect(ws.publishTo('/app/echo', 'hello')).toBe(false);

    ws.subscribeTo(DESTINATION, () => {});
    const client = clientAt();
    await client.connect();

    expect(ws.publishTo('/app/echo', 'hello')).toBe(true);
  });
});
