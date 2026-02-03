import { format } from 'date-fns';
import { useEffect } from 'react';

import { useWebSocket } from '@/components/providers/WebSocketProvider';
import { useGetMessages } from '@/features/message/api/get-messages';
import { useSession } from '@/lib/auth/client';
import { getQueryClient } from '@/lib/query';
import { cn } from '@/lib/utils';
import { Message } from '@/types/message';

interface MessagesListProps {
  to: string;
}

export default function MessagesList({ to }: MessagesListProps) {
  const { data: session } = useSession();
  const { subscribe } = useWebSocket();

  const queryClient = getQueryClient();

  const { data: messages } = useGetMessages(to);
  useEffect(() => {
    if (!session) {
      return;
    }

    const subscription = subscribe(
      `/topic/users/${session.user.id}/messages`,
      (message) => {
        queryClient.setQueryData(['messages', to], (oldMessages: Message[]) => {
          const newMessage: {
            messageId: string;
            content: string;
            senderId: string;
            sentAt: string;
          } = JSON.parse(message.body);

          return [
            ...(oldMessages || []),
            {
              id: newMessage.messageId,
              content: newMessage.content,
              senderId: newMessage.senderId,
              sentAt: new Date(newMessage.sentAt),
            } satisfies Message,
          ];
        });
      },
    );

    return () => {
      subscription?.unsubscribe();
    };
  }, [session, queryClient, to, subscribe]);

  if (!messages || !session?.user) {
    return null;
  }

  return (
    <div>
      {messages.map((message) => {
        const isOwn = message.senderId === session?.user.id;

        return (
          <div
            key={message.id}
            className={cn('flex', isOwn ? 'justify-end' : 'justify-start')}
          >
            <div className={`max-w-md ${isOwn ? 'order-2' : 'order-1'}`}>
              <div
                className={`px-4 py-2 rounded-2xl ${
                  message.senderId === session?.user.id
                    ? 'bg-muted rounded-br-sm'
                    : 'bg-primary text-white rounded-bl-sm'
                }`}
              >
                <p>{message.content}</p>
              </div>
              <p className="text-xs mt-1">
                {format(message.sentAt, 'MM-dd HH:mm')}
              </p>
            </div>
          </div>
        );
      })}
    </div>
  );
}
