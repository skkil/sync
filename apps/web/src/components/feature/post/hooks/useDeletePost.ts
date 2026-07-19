import { useQueryClient } from '@tanstack/react-query';

import { useDeletePost as useDeletePostMutation } from '@/api/__generated__/post/post';

import { invalidatePostQueries } from './postQueryKeys';

export function useDeletePost() {
  const queryClient = useQueryClient();

  return useDeletePostMutation({
    mutation: {
      onSuccess: () => invalidatePostQueries(queryClient),
    },
  });
}
