import { useMutation } from '@tanstack/react-query';

import { server } from '@/lib/server';

async function unfollowUser(userId: string) {
  return server.delete<void>(`users/unfollow/${userId}`);
}

export function useUnfollowUserMutation() {
  return useMutation({
    mutationFn: unfollowUser,
  });
}
