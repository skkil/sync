import { useQueryClient } from '@tanstack/react-query';

import {
  getGetPostCommentsInfiniteQueryKey,
  type getPostCommentsResponse,
  useCreateComment as useCreateCommentMutation,
} from '@/api/__generated__/comment/comment';

import { COMMENT_PAGE_SIZE } from '../viewer/constants';

export function useCreateComment() {
  const queryClient = useQueryClient();

  return useCreateCommentMutation({
    mutation: {
      onSuccess: async (_data, { slug }) => {
        const commentsQueryKey = getGetPostCommentsInfiniteQueryKey(slug, {
          first: COMMENT_PAGE_SIZE,
        });

        // Drop every page but the first so the refetch below starts clean,
        // rather than re-requesting every page the user had scrolled through.
        queryClient.setQueryData<{
          pages: getPostCommentsResponse[];
          pageParams: unknown[];
        }>(commentsQueryKey, (previous) => {
          if (!previous) {
            return previous;
          }

          return {
            pages: previous.pages.slice(0, 1),
            pageParams: previous.pageParams.slice(0, 1),
          };
        });

        await queryClient.invalidateQueries({ queryKey: commentsQueryKey });
      },
    },
  });
}
