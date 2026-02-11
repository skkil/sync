import { UseQueryOptions, useQuery } from '@tanstack/react-query';

import { server } from '@/lib/server';
import { Profile } from '@/types/profile';

export interface GetProfileResponse {
  userId: string;
  name: string;
  email: string;
  bio: string;
}

export async function getProfile(userId: string) {
  return server
    .get<GetProfileResponse>(`profiles/${userId}`)
    .json()
    .then((data) => ({
      id: data.userId,
      name: data.name,
      email: data.email,
      bio: data.bio,
    }));
}

export const getProfileQueryOptions = (
  userId: string,
): UseQueryOptions<Profile> => ({
  queryKey: ['profile', userId],
  queryFn: () => getProfile(userId),
});

export function useGetProfileQuery(userId: string) {
  return useQuery<Profile>(getProfileQueryOptions(userId));
}
