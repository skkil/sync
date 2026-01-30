import { useQuery } from '@tanstack/react-query';

import { url } from '@/lib/server';
import { server } from '@/lib/server/client';
import { Message } from '@/types/message';

interface GetMessagesResponse {
  messages: {
    content: {
      messageId: string;
      content: string;
      senderId: string;
      sentAt: Date;
    }[];
  };
}

async function getMessages(participantId: string): Promise<Message[]> {
  return server
    .get<GetMessagesResponse>(
      url('conversations/messages', {
        participantId,
      }),
    )
    .json()
    .then((data) =>
      data.messages.content.map((message) => ({
        id: message.messageId,
        content: message.content,
        senderId: message.senderId,
        sentAt: new Date(message.sentAt),
      })),
    );
}

export function useGetMessages(participantId: string) {
  return useQuery({
    queryKey: ['messages', participantId],
    queryFn: () => getMessages(participantId),
  });
}
