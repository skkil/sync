import { useQueryClient } from '@tanstack/react-query';

import {
  getGetPostCommentsInfiniteQueryKey,
  useAcceptComment as useAcceptCommentMutation,
  useUnacceptComment as useUnacceptCommentMutation,
} from '@/api/__generated__/comment/comment';

import { COMMENT_PAGE_SIZE } from '../constants';

export function useCommentAcceptance(slug: string) {
  const queryClient = useQueryClient();

  const invalidateComments = () =>
    queryClient.invalidateQueries({
      queryKey: getGetPostCommentsInfiniteQueryKey(slug, {
        first: COMMENT_PAGE_SIZE,
      }),
    });

  const { mutate: acceptComment, isPending: isAccepting } =
    useAcceptCommentMutation({
      mutation: { onSuccess: invalidateComments },
    });

  const { mutate: unacceptComment, isPending: isUnaccepting } =
    useUnacceptCommentMutation({
      mutation: { onSuccess: invalidateComments },
    });

  return {
    acceptComment: (commentId: string) => acceptComment({ commentId }),
    unacceptComment: (commentId: string) => unacceptComment({ commentId }),
    isPending: isAccepting || isUnaccepting,
  };
}
