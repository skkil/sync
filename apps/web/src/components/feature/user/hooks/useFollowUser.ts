import { QueryClient, useQuery, useQueryClient } from '@tanstack/react-query';

import type { GetProfileResponse } from '@/api/__generated__/types/GetProfileResponse';
import {
  useFollowUser as useFollowUserMutation,
  useUnfollowUser as useUnfollowUserMutation,
} from '@/api/__generated__/user/user';

/**
 * Recommendation-style endpoints (e.g. GetRecommendations) don't return an
 * `isFollowing` flag, so followed state for those lists is tracked here
 * instead, keyed in the query cache so it survives remounts.
 */
export const FOLLOWED_RECOMMENDED_USER_IDS_QUERY_KEY = [
  'user',
  'followedRecommendedUserIds',
];

export function useFollowedRecommendedUserIds() {
  const { data = [] } = useQuery<string[]>({
    queryKey: FOLLOWED_RECOMMENDED_USER_IDS_QUERY_KEY,
    queryFn: () => [],
    staleTime: Infinity,
    gcTime: Infinity,
  });

  return data;
}

function isProfileQueryKey(queryKey: readonly unknown[]) {
  return (
    typeof queryKey[0] === 'string' && queryKey[0].startsWith('/profiles/')
  );
}

function setProfileFollowingState(
  queryClient: QueryClient,
  followeeId: string,
  isFollowing: boolean,
) {
  queryClient.setQueriesData<{ data: GetProfileResponse }>(
    { predicate: (query) => isProfileQueryKey(query.queryKey) },
    (old) => {
      if (!old || old.data.userId !== followeeId) {
        return old;
      }

      return {
        ...old,
        data: {
          ...old.data,
          isFollowing,
          followerCount: old.data.followerCount + (isFollowing ? 1 : -1),
        },
      };
    },
  );
}

function addFollowedRecommendedUserId(
  queryClient: QueryClient,
  followeeId: string,
) {
  queryClient.setQueryData<string[]>(
    FOLLOWED_RECOMMENDED_USER_IDS_QUERY_KEY,
    (prev = []) => (prev.includes(followeeId) ? prev : [...prev, followeeId]),
  );
}

function removeFollowedRecommendedUserId(
  queryClient: QueryClient,
  followeeId: string,
) {
  queryClient.setQueryData<string[]>(
    FOLLOWED_RECOMMENDED_USER_IDS_QUERY_KEY,
    (prev = []) => prev.filter((id) => id !== followeeId),
  );
}

export function useFollowUser() {
  const queryClient = useQueryClient();

  return useFollowUserMutation({
    mutation: {
      onSuccess: (_data, { followeeId }) => {
        setProfileFollowingState(queryClient, followeeId, true);
        addFollowedRecommendedUserId(queryClient, followeeId);
      },
    },
  });
}

export function useUnfollowUser() {
  const queryClient = useQueryClient();

  return useUnfollowUserMutation({
    mutation: {
      onSuccess: (_data, { followeeId }) => {
        setProfileFollowingState(queryClient, followeeId, false);
        removeFollowedRecommendedUserId(queryClient, followeeId);
      },
    },
  });
}
