import { UseQueryOptions, useQuery } from '@tanstack/react-query';

import { server } from '@/lib/server';
import { Conversation } from '@/types/message';

export type GetConversationsResponse = {
  conversations: {
    conversationId: string;
    participants: {
      userId: string;
      name: string;
    }[];
    lastMessage?: {
      messageId: string;
      senderId: string;
      content: string;
      sentAt: string;
    };
  }[];
};

export async function getConversations(): Promise<Conversation[]> {
  return server
    .get<GetConversationsResponse>('conversations')
    .json()
    .then((data) =>
      data.conversations.map((conversation) => ({
        id: conversation.conversationId,
        participants: conversation.participants.map((participant) => ({
          id: participant.userId,
          name: participant.name,
        })),
        lastMessage: conversation.lastMessage && {
          id: conversation.lastMessage.messageId,
          senderId: conversation.lastMessage.senderId,
          content: conversation.lastMessage.content,
          sentAt: new Date(conversation.lastMessage.sentAt),
        },
      })),
    );
}

export const useGetConversationsQueryOptions = (): UseQueryOptions<
  Conversation[]
> => ({
  queryKey: ['conversations'],
  queryFn: () => getConversations(),
});

export function useGetConversationsQuery() {
  return useQuery<Conversation[]>(useGetConversationsQueryOptions());
}
