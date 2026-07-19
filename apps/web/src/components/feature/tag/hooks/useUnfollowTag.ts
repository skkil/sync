import { useQueryClient } from '@tanstack/react-query';

import {
  getGetFollowedTagsQueryKey,
  useUnfollowTag as useUnfollowTagMutation,
} from '@/api/__generated__/tag/tag';

interface UseUnfollowTagOptions {
  handle: string;
  onSettled?: () => void;
}

export function useUnfollowTag(options: UseUnfollowTagOptions) {
  const queryClient = useQueryClient();

  return useUnfollowTagMutation({
    mutation: {
      onSettled: async () => {
        await queryClient.invalidateQueries({
          queryKey: getGetFollowedTagsQueryKey(options.handle),
        });

        options.onSettled?.();
      },
    },
  });
}
