import { type QueryClient, useQueryClient } from '@tanstack/react-query';

import {
  getGetFollowedProjectsQueryKey,
  getGetMyProjectsQueryKey,
  getGetProjectByHandleQueryOptions,
  getGetProjectRecommendationsQueryKey,
  useFollowProject as useFollowProjectMutation,
  useUnfollowProject as useUnfollowProjectMutation,
} from '@/api/__generated__/project/project';
import type { GetProjectsResponse } from '@/api/__generated__/types/GetProjectsResponse';
import { getGetUserRecommendationsQueryKey } from '@/api/__generated__/user/user';
import { invalidatePostRecommendationQueries } from '@/components/feature/post/hooks/postQueryKeys';
import { useSession } from '@/lib/auth/client';

function invalidateProjectFollowRecommendations(queryClient: QueryClient) {
  queryClient.invalidateQueries({
    queryKey: getGetProjectRecommendationsQueryKey(),
  });
  queryClient.invalidateQueries({
    queryKey: getGetUserRecommendationsQueryKey(),
  });
  invalidatePostRecommendationQueries(queryClient);
}

export function useFollowProject() {
  const queryClient = useQueryClient();
  const { data: session } = useSession();

  return useFollowProjectMutation({
    mutation: {
      onSuccess: (_data, { handle }) => {
        // The project header reads `isFollowing` off the project detail, so
        // refresh it whenever the follow state changes.
        queryClient.invalidateQueries(
          getGetProjectByHandleQueryOptions(handle),
        );
        queryClient.invalidateQueries({
          queryKey: getGetMyProjectsQueryKey(),
        });
        invalidateProjectFollowRecommendations(queryClient);

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
        // Keep the project detail's `isFollowing` in sync with the toggle.
        queryClient.invalidateQueries(
          getGetProjectByHandleQueryOptions(handle),
        );
        queryClient.invalidateQueries({
          queryKey: getGetMyProjectsQueryKey(),
        });
        invalidateProjectFollowRecommendations(queryClient);

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
