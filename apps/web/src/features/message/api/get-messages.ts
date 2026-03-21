import { useQuery } from '@tanstack/react-query';

import { server } from '@/lib/server';
import { Message } from '@/types/message';
import { url } from '@/util/server';

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

async function getMessages(conversationId: string): Promise<Message[]> {
  return server
    .get<GetMessagesResponse>(url(`conversations/${conversationId}/messages`))
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

export function useGetMessagesQuery(conversationId: string) {
  return useQuery({
    queryKey: ['conversations', conversationId, 'messages'],
    queryFn: () => getMessages(conversationId),
  });
}
