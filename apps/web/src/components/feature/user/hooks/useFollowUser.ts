import { QueryClient, useQuery, useQueryClient } from '@tanstack/react-query';

import type { GetProfileResponse } from '@/api/__generated__/types/GetProfileResponse';
import {
  getGetUserRecommendationsQueryKey,
  useFollowUser as useFollowUserMutation,
  useUnfollowUser as useUnfollowUserMutation,
} from '@/api/__generated__/user/user';
import { invalidatePostRecommendationQueries } from '@/components/feature/post/hooks/postQueryKeys';

/**
 * 추천 API 응답에는 `isFollowing`이 없으므로 추천 목록의 팔로우 상태를 쿼리 캐시에 별도로 보관한다.
 * 컴포넌트가 다시 마운트되어도 같은 상태를 유지하기 위한 캐시다.
 */
export const FOLLOWED_RECOMMENDED_USER_IDS_QUERY_KEY = [
  'user',
  'followedRecommendedUserIds',
];

interface UseUserFollowOptions {
  invalidateUserRecommendations?: boolean;
}

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

export function useFollowUser({
  invalidateUserRecommendations = true,
}: UseUserFollowOptions = {}) {
  const queryClient = useQueryClient();

  return useFollowUserMutation({
    mutation: {
      onSuccess: (_data, { followeeId }) => {
        setProfileFollowingState(queryClient, followeeId, true);
        addFollowedRecommendedUserId(queryClient, followeeId);
        if (invalidateUserRecommendations) {
          queryClient.invalidateQueries({
            queryKey: getGetUserRecommendationsQueryKey(),
          });
        }
        invalidatePostRecommendationQueries(queryClient);
      },
    },
  });
}

export function useUnfollowUser({
  invalidateUserRecommendations = true,
}: UseUserFollowOptions = {}) {
  const queryClient = useQueryClient();

  return useUnfollowUserMutation({
    mutation: {
      onSuccess: (_data, { followeeId }) => {
        setProfileFollowingState(queryClient, followeeId, false);
        removeFollowedRecommendedUserId(queryClient, followeeId);
        if (invalidateUserRecommendations) {
          queryClient.invalidateQueries({
            queryKey: getGetUserRecommendationsQueryKey(),
          });
        }
        invalidatePostRecommendationQueries(queryClient);
      },
    },
  });
}
