import { useQueryClient } from '@tanstack/react-query';

import {
  getGetFollowedProjectsQueryKey,
  useFollowProject as useFollowProjectMutation,
  useUnfollowProject as useUnfollowProjectMutation,
} from '@/api/__generated__/project/project';
import type { GetProjectsResponse } from '@/api/__generated__/types/GetProjectsResponse';
import { useSession } from '@/lib/auth/client';

export function useFollowProject() {
  const queryClient = useQueryClient();
  const { data: session } = useSession();

  return useFollowProjectMutation({
    mutation: {
      onSuccess: () => {
        if (!session?.user.handle) {
          return;
        }

        // Followed project's full summary isn't returned by the follow
        // mutation, so the list can't be patched optimistically — refetch it.
        queryClient.invalidateQueries({
          queryKey: getGetFollowedProjectsQueryKey(session.user.handle),
        });
      },
    },
  });
}

export function useUnfollowProject() {
  const queryClient = useQueryClient();
  const { data: session } = useSession();

  return useUnfollowProjectMutation({
    mutation: {
      onSuccess: (_data, { handle }) => {
        if (!session?.user.handle) {
          return;
        }

        queryClient.setQueryData<{ data: GetProjectsResponse }>(
          getGetFollowedProjectsQueryKey(session.user.handle),
          (old) => {
            if (!old) {
              return old;
            }

            return {
              ...old,
              data: {
                projects: old.data.projects.filter((p) => p.handle !== handle),
              },
            };
          },
        );
      },
    },
  });
}
