import {
  UseMutationOptions,
  useMutation,
  useQueryClient,
} from '@tanstack/react-query';

import { useUploadProfileImageMutation } from '@/features/media/api/upload-profile-image';
import { server } from '@/lib/server';

export interface UpdateProfileRequest {
  name?: string;
  profession?: string;
  isOnboarded?: boolean;
  bio?: string;
  profileImageId?: number;
  removeProfileImage?: boolean;
}

async function updateProfile(request: UpdateProfileRequest) {
  return server.patch<void>('profiles/me', {
    json: request,
  });
}

type UpdateProfileMutationOptions = Omit<
  UseMutationOptions<void, Error, UpdateProfileRequest>,
  'mutationFn'
>;

export function useUpdateProfileMutation(options?: UpdateProfileMutationOptions) {
  const queryClient = useQueryClient();

  return useMutation({
    mutationFn: async (request) => {
      await updateProfile(request);
    },
    ...options,
    onSuccess: async (data, variables, onMutateResult, context) => {
      await queryClient.invalidateQueries({ queryKey: ['profile'] });
      await options?.onSuccess?.(data, variables, onMutateResult, context);
    },
  });
}

export interface UpdateProfileWithImageRequest
  extends Omit<UpdateProfileRequest, 'profileImageId'> {
  profileImageFile?: File | null;
}

type UpdateProfileWithImageMutationOptions = Omit<
  UseMutationOptions<void, Error, UpdateProfileWithImageRequest>,
  'mutationFn'
>;

export function useUpdateProfileWithImageMutation(
  options?: UpdateProfileWithImageMutationOptions,
) {
  const { mutateAsync: uploadProfileImage, isPending: isUploadingProfileImage } =
    useUploadProfileImageMutation();
  const { mutateAsync: updateProfile, isPending: isUpdatingProfile } =
    useUpdateProfileMutation();

  const mutation = useMutation({
    mutationFn: async ({ profileImageFile, ...request }) => {
      let profileImageId: number | undefined;

      if (profileImageFile) {
        profileImageId = await uploadProfileImage(profileImageFile);
      }

      await updateProfile({
        ...request,
        profileImageId,
        removeProfileImage:
          profileImageFile != null ? false : request.removeProfileImage,
      });
    },
    ...options,
  });

  return {
    ...mutation,
    isPending:
      mutation.isPending || isUpdatingProfile || isUploadingProfileImage,
  };
}
