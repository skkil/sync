import { useMutation } from '@tanstack/react-query';

import { server } from '@/lib/server/client';

interface UpdateProfileRequest {
  name?: string;
  profileImageId?: string;
  isOnboarded?: boolean;
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
