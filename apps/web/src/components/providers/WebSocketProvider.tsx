'use client';

import { Client } from '@stomp/stompjs';
import { useTranslations } from 'next-intl';
import { createContext, useContext, useEffect, useRef, useState } from 'react';
import { toast } from 'sonner';

import { IncomingMessageWebSocketEventHandler } from '@/features/message/handler/IncomingMessageWebSocketEventHandler';
import { useSession } from '@/lib/auth/client';
import { getStompClient } from '@/lib/ws/client';
import { WebSocketEventManager } from '@/lib/ws/manager';

enum WebSocketConnectionStatus {
  CONNECTING,
  CONNECTED,
  FAILED,
}

interface WebSocketContextType {
  publish: (destination: string, body: string) => void;
}

const WebSocketContext = createContext<WebSocketContextType | null>(null);

interface WebSocketProviderProps {
  children: React.ReactNode;
}

export default function WebSocketProvider({
  children,
}: WebSocketProviderProps) {
  const t = useTranslations();

  const { data: session } = useSession();

  const [connectionStatus, setConnectionStatus] =
    useState<WebSocketConnectionStatus>(WebSocketConnectionStatus.CONNECTING);

  const clientRef = useRef<Client | null>(null);
  const managerRef = useRef<WebSocketEventManager | null>(null);

  useEffect(() => {
    if (!clientRef.current) {
      clientRef.current = getStompClient();
      clientRef.current.onConnect = () => {
        setConnectionStatus(WebSocketConnectionStatus.CONNECTED);
        if (session?.user) {
          managerRef.current?.subscribe(session.user.id);
        }
      };

      managerRef.current = new WebSocketEventManager(clientRef.current);

      managerRef.current.registerHandler(
        new IncomingMessageWebSocketEventHandler(),
      );
    }

    clientRef.current.activate();

    return () => {
      if (clientRef.current) {
        clientRef.current.deactivate();
      }
    };
  }, [session]);

  useEffect(() => {
    if (!session || !session.user) {
      return;
    }

    if (connectionStatus === WebSocketConnectionStatus.CONNECTED) {
      managerRef.current?.subscribe(session.user.id);
    }

    return () => {
      managerRef.current?.unsubscribeAll();
    };
  }, [session, connectionStatus]);

  return (
    <WebSocketContext.Provider
      value={{
        publish(destination, body) {
          if (clientRef.current && clientRef.current.connected) {
            clientRef.current.publish({ destination, body });
          } else {
            toast.error(t('errors.connection-failed'));
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
