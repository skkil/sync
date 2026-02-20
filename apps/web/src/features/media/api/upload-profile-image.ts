import { useMutation } from '@tanstack/react-query';

import { server } from '@/lib/server';

export const PROFILE_IMAGE_MAX_SIZE_BYTES = 5 * 1024 * 1024;

export const PROFILE_IMAGE_ALLOWED_TYPES = ['image/jpeg', 'image/png'] as const;

interface UploadMediaRequest {
  mediaType: string;
  mediaContext: 'profile-image';
  fileName: string;
  fileSize: number;
}

interface UploadMediaResponse {
  mediaId: number;
  uploadUrl: string;
  expiresAt: string;
}

async function uploadProfileImage(file: File): Promise<number> {
  const request: UploadMediaRequest = {
    mediaType: file.type,
    mediaContext: 'profile-image',
    fileName: file.name,
    fileSize: file.size,
  };

  const { mediaId, uploadUrl } = await server
    .post<UploadMediaResponse>('media', { json: request })
    .json();

  const response = await fetch(uploadUrl, {
    method: 'PUT',
    body: file,
  });

  if (!response.ok) {
    throw new Error('Failed to upload profile image');
  }

  return mediaId;
}

export function useUploadProfileImageMutation() {
  return useMutation({
    mutationFn: uploadProfileImage,
  });
}
