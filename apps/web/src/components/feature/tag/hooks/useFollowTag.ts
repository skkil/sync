import { useQueryClient } from '@tanstack/react-query';

import {
  getGetFollowedTagsQueryKey,
  useFollowTag as useFollowTagMutation,
} from '@/api/__generated__/tag/tag';

interface UseFollowTagOptions {
  handle: string;
  onSettled?: () => void;
}

export function useFollowTag(options: UseFollowTagOptions) {
  const queryClient = useQueryClient();

  return useFollowTagMutation({
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
