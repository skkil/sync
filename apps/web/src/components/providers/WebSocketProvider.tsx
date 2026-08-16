'use client';

import { IMessage, StompSubscription } from '@stomp/stompjs';
import { useTranslations } from 'next-intl';
import { createContext, useCallback, useContext, useMemo } from 'react';
import { toast } from 'sonner';

import { type SubscribeOptions, publishTo, subscribeTo } from '@/lib/ws';

const WEBSOCKET_ENABLED = process.env.NEXT_PUBLIC_WEBSOCKET_ENABLED === 'true';

interface WebSocketContextType {
  publish: (destination: string, body: string) => void;
  subscribe: (
    destination: string,
    callback: (message: IMessage) => void,
    options?: SubscribeOptions,
  ) => StompSubscription | undefined;
}

const WebSocketContext = createContext<WebSocketContextType | null>(null);

interface WebSocketProviderProps {
  children: React.ReactNode;
}

/**
 * STOMP 연결은 이 컴포넌트가 아니라 `@/lib/ws`의 모듈 싱글턴이 소유한다.
 * 연결은 구독자가 생길 때 시작되고 마지막 구독자가 사라지면 끊긴다(lazy
 * activation). 익명 사용자는 알림을 구독하지 않으므로 핸드셰이크 자체가
 * 일어나지 않는다.
 */
export default function WebSocketProvider({
  children,
}: WebSocketProviderProps) {
  const t = useTranslations();

  // 연결 전에 호출돼도 구독을 예약해두고 연결 직후·재연결마다 자동으로
  // 다시 건다. 따라서 "아직 연결 안 됨"은 오류가 아니다.
  //
  // identity를 영구 고정한다 — useNotifications의 effect deps에 들어가므로,
  // 여기가 흔들리면 렌더마다 실제 UNSUBSCRIBE/SUBSCRIBE 프레임 쌍이 오간다.
  // next-intl `t`의 (문서화되지 않은) identity 안정성에 기대지 않는다.
  const subscribe = useCallback<WebSocketContextType['subscribe']>(
    (destination, callback, options) => {
      if (!WEBSOCKET_ENABLED) {
        return undefined;
      }

      return subscribeTo(destination, callback, options);
    },
    [],
  );

  const publish = useCallback<WebSocketContextType['publish']>(
    (destination, body) => {
      if (!WEBSOCKET_ENABLED) {
        return;
      }

      if (!publishTo(destination, body)) {
        toast.error(t('errors.connection-failed'));
      }
    },
    [t],
  );

  const value = useMemo<WebSocketContextType>(
    () => ({ publish, subscribe }),
    [publish, subscribe],
  );

  return (
    <WebSocketContext.Provider value={value}>
      {children}
    </WebSocketContext.Provider>
  );
}

export function useWebSocket() {
  const context = useContext(WebSocketContext);
  if (!context) {
    throw new Error('useWebSocket must be used within WebSocketProvider');
  }

  return context;
}
