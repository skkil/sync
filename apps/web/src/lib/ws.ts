import { Client } from '@stomp/stompjs';
import SockJS from 'sockjs-client';

import { env } from './env';
import { isServer } from './server';

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
    debug: (message) => {
      console.log(`[STOMP] ${message}`);
    },
    reconnectDelay: 5000,
    heartbeatIncoming: 4000,
    heartbeatOutgoing: 4000,
  });

  return client;
}
