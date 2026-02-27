import { format } from 'date-fns';

import { useGetMessagesQuery } from '@/features/message/api/get-messages';
import { useSession } from '@/lib/auth/client';
import { cn } from '@/lib/utils';

interface MessagesListProps {
  conversationId: string;
}

export default function MessagesList({ conversationId }: MessagesListProps) {
  const { data: session } = useSession();

  const { data: messages } = useGetMessagesQuery(conversationId);

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
