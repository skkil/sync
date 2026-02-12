import { useMutation } from '@tanstack/react-query';

import { GetProfileQueryResponse } from '@/features/profile/api/get-profile';
import { server } from '@/lib/server';

async function followUser(userId: string) {
  return server.post<void>(`users/follow/${userId}`);
}

export function useFollowUserMutation() {
  return useMutation({
    mutationFn: followUser,
    onMutate: (userId, context) => {
      const profile: GetProfileQueryResponse | undefined =
        context.client.getQueryData(['profile', userId]);

      context.client.setQueryData(['profile', userId], {
        ...profile,
        isFollowing: true,
      });
    },
  });
}
