import { useMutation } from '@tanstack/react-query';

import { GetProfileQueryResponse } from '@/features/profile/api/get-profile';
import { server } from '@/lib/server';

async function unfollowUser(userId: string) {
  return server.delete<void>(`users/unfollow/${userId}`);
}

export function useUnfollowUserMutation() {
  return useMutation({
    mutationFn: unfollowUser,
    onMutate: (userId, context) => {
      const profile: GetProfileQueryResponse | undefined =
        context.client.getQueryData(['profile', userId]);

      context.client.setQueryData(['profile', userId], {
        ...profile,
        isFollowing: false,
      });
    },
  });
}
