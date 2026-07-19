import { useQueryClient } from '@tanstack/react-query';

import { useUnbookmarkPost as useUnbookmarkPostMutation } from '@/api/__generated__/bookmark/bookmark';

import { invalidatePostQueries } from './postQueryKeys';

export function useUnbookmarkPost() {
  const queryClient = useQueryClient();

  return useUnbookmarkPostMutation({
    mutation: {
      onSuccess: () => invalidatePostQueries(queryClient),
    },
  });
}
