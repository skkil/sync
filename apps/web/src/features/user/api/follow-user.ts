import { useMutation } from '@tanstack/react-query';

import { server } from '@/lib/server';

async function followUser(userId: string) {
  return server.post<void>(`users/follow/${userId}`);
}

export function useFollowUserMutation() {
  return useMutation({
    mutationFn: followUser,
  });
}
