import { useQueryClient } from '@tanstack/react-query';

import {
  getGetFollowedTagsQueryKey,
  getGetTagRecommendationsQueryKey,
  useUnfollowTag as useUnfollowTagMutation,
} from '@/api/__generated__/tag/tag';
import { invalidatePostRecommendationQueries } from '@/components/feature/post/hooks/postQueryKeys';

interface UseUnfollowTagOptions {
  handle: string;
  onSettled?: () => void;
}

export function useUnfollowTag(options: UseUnfollowTagOptions) {
  const queryClient = useQueryClient();

  return useUnfollowTagMutation({
    mutation: {
      onSuccess: () => {
        queryClient.invalidateQueries({
          queryKey: getGetTagRecommendationsQueryKey(),
        });
        invalidatePostRecommendationQueries(queryClient);
      },
      onSettled: async () => {
        await queryClient.invalidateQueries({
          queryKey: getGetFollowedTagsQueryKey(options.handle),
        });

        options.onSettled?.();
      },
    },
  });
}
