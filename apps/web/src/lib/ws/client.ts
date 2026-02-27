'use client';

import { Client } from '@stomp/stompjs';
import SockJS from 'sockjs-client';

import { env } from '@/lib/env';
import { getCsrfToken } from '@/util/cookie';
import { isServer } from '@/util/server';

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
    connectHeaders: {
      'X-XSRF-TOKEN': getCsrfToken(),
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
