import { useState } from 'react';

import { useUploadMedia } from '@/api/__generated__/media/media';
import { uploadFileToS3 } from '@/api/s3';
import { useUpdateProfile } from '@/components/feature/profile/hooks/useUpdateProfile';
import { FileInputError } from '@/components/ui/input';
import { useSession } from '@/lib/auth/client';
import SyncError, { ErrorCode } from '@/lib/error';

export type ProfileImageUploadErrorCode =
  | 'maxSize'
  | 'unsupportedType'
  | 'uploadFailed'
  | 'network'
  | 'custom';

export interface ProfileImageUploadError {
  code: ProfileImageUploadErrorCode;
  message?: string;
}

interface UseProfileImageUploadOptions {
  handle: string;
  onUploadSuccess?: () => void;
}

export function useProfileImageUpload({
  handle,
  onUploadSuccess,
}: UseProfileImageUploadOptions) {
  const { refetch: refetchSession } = useSession();

  const { mutate: updateProfile } = useUpdateProfile({
    handle,
    onSuccess: refetchSession,
  });

  const { mutateAsync: uploadMedia, isPending: isUploadMediaPending } =
    useUploadMedia();

  const [selectedImage, setSelectedImage] = useState<{
    file: File;
    src: string;
  } | null>(null);
  const [error, setError] = useState<ProfileImageUploadError | null>(null);

  const handleFileError = (fileInputError: FileInputError) => {
    if (fileInputError === 'size') {
      setError({ code: 'maxSize' });
    } else if (fileInputError === 'type') {
      setError({ code: 'unsupportedType' });
    }
  };

  const handleFileChange = async (files: File[]) => {
    const file = files[0];
    if (!file) {
      return;
    }

    setSelectedImage({
      file,
      src: URL.createObjectURL(file),
    });
    setError(null);

    const {
      data: { uploadUrl, mediaId },
    } = await uploadMedia({
      data: {
        fileName: file.name,
        fileSize: file.size,
        mediaType: file.type,
      },
    });

    const { success: uploadSuccess } = await uploadFileToS3({
      file,
      uploadUrl,
    });

    if (!uploadSuccess) {
      setError({ code: 'uploadFailed' });
      setSelectedImage(null);
      return;
    }

    updateProfile(
      {
        data: {
          profileImageId: mediaId,
        },
      },
      {
        onSuccess: () => {
          setSelectedImage(null);
          onUploadSuccess?.();
        },
        onError: (uploadError) => {
          if (
            uploadError instanceof SyncError &&
            uploadError.code === ErrorCode.NETWORK_ERROR
          ) {
            setError({ code: 'network' });
          } else if (uploadError instanceof SyncError) {
            setError({ code: 'custom', message: uploadError.message });
          } else {
            setError({ code: 'uploadFailed' });
          }
        },
      },
    );
  };

  const handleRemoveImage = () => {
    setSelectedImage(null);
    updateProfile({
      data: {
        removeProfileImage: true,
      },
    });
  };

  return {
    selectedImage,
    error,
    isUploadMediaPending,
    handleFileChange,
    handleFileError,
    handleRemoveImage,
  };
}
