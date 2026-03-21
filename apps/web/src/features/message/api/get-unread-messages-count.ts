import { useQuery } from '@tanstack/react-query';

import { server } from '@/lib/server';

interface GetUnreadMessagesCountResponse {
  conversations: {
    conversationId: string;
    unreadCount: number;
  }[];
}

async function getUnreadMessagesCount() {
  return server
    .get<GetUnreadMessagesCountResponse>('conversations/unread')
    .json()
    .then((data) => data.conversations);
}

export function useGetUnreadMessagesCountQuery() {
  return useQuery({
    queryKey: ['unreadMessagesCount'],
    queryFn: getUnreadMessagesCount,
  });
}
