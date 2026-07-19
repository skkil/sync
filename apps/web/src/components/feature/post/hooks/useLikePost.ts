import { useQueryClient } from '@tanstack/react-query';

import { useLikePost as useLikePostMutation } from '@/api/__generated__/post/post';

import { invalidatePostQueries } from './postQueryKeys';

export function useLikePost() {
  const queryClient = useQueryClient();

  return useLikePostMutation({
    mutation: {
      onSuccess: () => invalidatePostQueries(queryClient),
    },
  });
}
