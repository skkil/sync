import { useQueryClient } from '@tanstack/react-query';

import { useBookmarkPost as useBookmarkPostMutation } from '@/api/__generated__/bookmark/bookmark';

import { invalidatePostQueries } from './postQueryKeys';

export function useBookmarkPost() {
  const queryClient = useQueryClient();

  return useBookmarkPostMutation({
    mutation: {
      onSuccess: () => invalidatePostQueries(queryClient),
    },
  });
}
