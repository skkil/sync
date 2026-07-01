'use client';

import { Client, IMessage, StompSubscription } from '@stomp/stompjs';
import { useTranslations } from 'next-intl';
import { usePathname } from 'next/navigation';
import { createContext, useContext, useEffect, useRef } from 'react';
import { toast } from 'sonner';

import { getStompClient } from '@/lib/ws';

interface WebSocketContextType {
  publish: (destination: string, body: string) => void;
  subscribe: (
    destination: string,
    callback: (message: IMessage) => void,
  ) => StompSubscription | undefined;
}

const WebSocketContext = createContext<WebSocketContextType | null>(null);

interface WebSocketProviderProps {
  children: React.ReactNode;
}

export default function WebSocketProvider({
  children,
}: WebSocketProviderProps) {
  const t = useTranslations();
  const pathname = usePathname();
  const shouldConnect =
    pathname !== '/' &&
    !pathname.startsWith('/auth') &&
    !pathname.startsWith('/onboarding');

  const client = useRef<Client | null>(null);

  useEffect(() => {
    if (!shouldConnect) {
      return;
    }

    if (!client.current) {
      client.current = getStompClient();
    }

    client.current.activate();

    return () => {
      if (client.current) {
        client.current.deactivate();
      }
    };
  }, [client, shouldConnect]);

  return (
    <WebSocketContext.Provider
      value={{
        publish(destination, body) {
          if (client.current && client.current.connected) {
            client.current.publish({ destination, body });
          } else {
            toast.error(t('errors.connection-failed'));
          }
        },
        subscribe(destination, callback) {
          if (client.current && client.current.connected) {
            return client.current.subscribe(destination, callback);
          } else {
            toast.error(t('errors.connection-failed'));
            return undefined;
          }
        },
      }}
    >
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
