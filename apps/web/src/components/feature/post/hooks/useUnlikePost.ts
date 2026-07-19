import { useQueryClient } from '@tanstack/react-query';

import { useUnlikePost as useUnlikePostMutation } from '@/api/__generated__/post/post';

import { invalidatePostQueries } from './postQueryKeys';

export function useUnlikePost() {
  const queryClient = useQueryClient();

  return useUnlikePostMutation({
    mutation: {
      onSuccess: () => invalidatePostQueries(queryClient),
    },
  });
}
