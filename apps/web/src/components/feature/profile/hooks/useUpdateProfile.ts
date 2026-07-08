import { useQueryClient } from '@tanstack/react-query';

import {
  getGetAuthenticatedUserQueryKey,
  getGetProfileByHandleQueryOptions,
  useUpdateProfile as useUpdateProfileMutation,
} from '@/api/__generated__/profile/profile';

interface UseUpdateProfileOptions {
  /** Also invalidate a specific profile-by-handle query on success. */
  handle?: string;
  onSuccess?: () => void;
}

export function useUpdateProfile(options?: UseUpdateProfileOptions) {
  const queryClient = useQueryClient();

  return useUpdateProfileMutation({
    mutation: {
      onSuccess: async () => {
        await queryClient.invalidateQueries({
          queryKey: getGetAuthenticatedUserQueryKey(),
        });

        if (options?.handle) {
          await queryClient.invalidateQueries(
            getGetProfileByHandleQueryOptions(options.handle),
          );
        }

        options?.onSuccess?.();
      },
    },
  });
}
