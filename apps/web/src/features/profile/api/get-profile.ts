import { UseQueryOptions, useQuery } from '@tanstack/react-query';

import { server } from '@/lib/server';
import { Profile } from '@/types/profile';

export interface GetProfileResponse {
  userId: string;
  name: string;
  email: string;
  profession: string | null;
  bio: string | null;
  isFollowing: boolean;
  contacts?: {
    custom: string | null;
    linkedin: string | null;
    github: string | null;
    instagram: string | null;
    twitter: string | null;
  };
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
      ...data,
      id: data.userId,
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
