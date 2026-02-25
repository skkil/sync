import { useMutation } from '@tanstack/react-query';

import { server } from '@/lib/server';

interface UpdateProfileRequest {
  name?: string;
  handle?: string;
  profession?: string;
  isOnboarded?: boolean;
  bio?: string;
  profileImageId?: string;
}

async function updateProfile(request: UpdateProfileRequest) {
  return server.patch<void>('profiles/me', {
    json: request,
  });
}

export function useUpdateProfileMutation() {
  return useMutation({
    mutationFn: updateProfile,
  });
}
