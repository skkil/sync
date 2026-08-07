import { useQueryClient } from '@tanstack/react-query';

import {
  getGetFollowedTagsQueryKey,
  getGetTagRecommendationsQueryKey,
  useFollowTag as useFollowTagMutation,
} from '@/api/__generated__/tag/tag';
import { invalidatePostRecommendationQueries } from '@/components/feature/post/hooks/postQueryKeys';

interface UseFollowTagOptions {
  handle: string;
  onSettled?: () => void;
}

export function useFollowTag(options: UseFollowTagOptions) {
  const queryClient = useQueryClient();

  return useFollowTagMutation({
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
