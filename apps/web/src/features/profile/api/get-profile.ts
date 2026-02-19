import { UseQueryOptions, useQuery } from '@tanstack/react-query';

import { server } from '@/lib/server';
import { Profile } from '@/types/profile';

export interface GetProfileResponse {
  userId: string;
  name: string;
  email: string;
  profession: string | null;
  bio: string | null;
  profileImageUrl: string | null;
  isFollowing: boolean;
}

export type GetProfileQueryResponse = Profile & {
  isFollowing: boolean;
};

export async function getProfile(
  userId: string,
): Promise<Profile & { isFollowing: boolean }> {
  return server
    .get<GetProfileResponse>(`profiles/${userId}`)
    .json()
    .then((data) => ({
      id: data.userId,
      name: data.name,
      email: data.email,
      profession: data.profession,
      bio: data.bio,
      profileImageUrl: data.profileImageUrl,
      isFollowing: data.isFollowing,
    }));
}

export const getProfileQueryOptions = (
  userId: string,
): UseQueryOptions<GetProfileQueryResponse> => ({
  queryKey: ['profile', userId],
  queryFn: () => getProfile(userId),
  enabled: !!userId,
});

export function useGetProfileQuery(userId: string) {
  return useQuery(getProfileQueryOptions(userId));
}
