import { Client } from '@stomp/stompjs';
import SockJS from 'sockjs-client';

import { getCsrfToken, isServer } from '@/util/server';

import { env } from './env';

let client: Client | null = null;

export function getStompClient(): Client {
  if (isServer()) {
    throw new Error('getStompClient can only be called in the browser');
  }

  if (client) {
    return client;
  }

  const url = env.NEXT_PUBLIC_BACKEND_URL + '/ws';

  client = new Client({
    webSocketFactory: () => {
      return new SockJS(url);
    },
    // 서버가 STOMP CONNECT 프레임에서 CSRF 토큰을 검증한다(HTTP 요청과 동일한
    // X-XSRF-TOKEN 헤더). 토큰이 회전될 수 있으므로 재연결마다 새로 읽는다.
    beforeConnect: async (stompClient) => {
      const csrfToken = await getCsrfToken();
      if (csrfToken) {
        stompClient.connectHeaders = { 'X-XSRF-TOKEN': csrfToken };
      }
    },
    // 프레임에 알림 payload가 그대로 담기므로 프로덕션 콘솔에는 남기지 않는다.
    debug: (message) => {
      if (process.env.NODE_ENV !== 'production') {
        // eslint-disable-next-line no-console -- intentional STOMP protocol debug logging, pre-existing
        console.log(`[STOMP] ${message}`);
      }
    },
    reconnectDelay: 5000,
    heartbeatIncoming: 4000,
    heartbeatOutgoing: 4000,
  });

  return client;
}
