'use client';

import { useQueryClient } from '@tanstack/react-query';
import { useTranslations } from 'next-intl';
import { ChangeEvent, useRef, useState } from 'react';
import { toast } from 'sonner';

import {
  PROFILE_IMAGE_ALLOWED_TYPES,
  PROFILE_IMAGE_MAX_SIZE_BYTES,
  useUploadProfileImageMutation,
} from '@/features/media/api/upload-profile-image';
import { useUpdateProfileMutation } from '@/features/profile/api/update-profile';
import { useModal } from '@/hooks/store';
import { useSession } from '@/lib/auth/client';

import { Avatar, AvatarFallback, AvatarImage } from '../ui/avatar';
import { Button } from '../ui/button';
import {
  Dialog,
  DialogClose,
  DialogContent,
  DialogDescription,
  DialogFooter,
  DialogHeader,
  DialogTitle,
} from '../ui/dialog';
import { Input } from '../ui/input';
import { Label } from '../ui/label';

export default function SettingsModal() {
  const t = useTranslations('modals.settings');

  const { mutateAsync: updateProfile, isPending: isUpdatingProfile } =
    useUpdateProfileMutation();
  const { mutateAsync: uploadProfileImage, isPending: isUploadingProfile } =
    useUploadProfileImageMutation();

  const { isOpen, closeModal } = useModal();
  const queryClient = useQueryClient();

  const [editedName, setEditedName] = useState<string | null>(null);
  const [selectedProfileImageFile, setSelectedProfileImageFile] =
    useState<File | null>(null);
  const [profileImagePreviewUrl, setProfileImagePreviewUrl] = useState<
    string | null
  >(null);
  const [profileImageError, setProfileImageError] = useState<string | null>(
    null,
  );
  const [isProfileImageMarkedForRemoval, setIsProfileImageMarkedForRemoval] =
    useState(false);

  const fileInputRef = useRef<HTMLInputElement>(null);
  const { data: session, refetch: refetchSession } = useSession();
  const isSaving = isUpdatingProfile || isUploadingProfile;
  const name = editedName ?? session?.user?.name ?? '';
  const hasExistingProfileImage = Boolean(session?.user?.image);
  const profileImageSrc =
    profileImagePreviewUrl ??
    (isProfileImageMarkedForRemoval ? null : (session?.user?.image ?? null));

  const clearSelectedProfileImage = () => {
    setSelectedProfileImageFile(null);
    setProfileImageError(null);

    if (profileImagePreviewUrl) {
      URL.revokeObjectURL(profileImagePreviewUrl);
      setProfileImagePreviewUrl(null);
    }

    if (fileInputRef.current) {
      fileInputRef.current.value = '';
    }
  };

  const resetFormState = () => {
    setEditedName(null);
    setIsProfileImageMarkedForRemoval(false);
    clearSelectedProfileImage();
  };

  const onSelectProfileImageFile = (event: ChangeEvent<HTMLInputElement>) => {
    const file = event.target.files?.[0];
    if (!file) {
      return;
    }

    if (!PROFILE_IMAGE_ALLOWED_TYPES.includes(file.type as never)) {
      setProfileImageError(t('profile.image.errors.unsupportedType'));
      event.target.value = '';
      return;
    }

    if (file.size > PROFILE_IMAGE_MAX_SIZE_BYTES) {
      setProfileImageError(t('profile.image.errors.maxSize'));
      event.target.value = '';
      return;
    }

    if (profileImagePreviewUrl) {
      URL.revokeObjectURL(profileImagePreviewUrl);
    }

    setSelectedProfileImageFile(file);
    setProfileImagePreviewUrl(URL.createObjectURL(file));
    setIsProfileImageMarkedForRemoval(false);
    setProfileImageError(null);
  };

  const onClickProfileImageAction = () => {
    if (selectedProfileImageFile) {
      clearSelectedProfileImage();
      return;
    }

    if (hasExistingProfileImage) {
      setIsProfileImageMarkedForRemoval((prev) => !prev);
      setProfileImageError(null);
    }
  };

  const onSave = async () => {
    try {
      let profileImageId: number | undefined;

      if (selectedProfileImageFile) {
        profileImageId = await uploadProfileImage(selectedProfileImageFile);
      }

      await updateProfile({
        name,
        profileImageId,
        removeProfileImage:
          !selectedProfileImageFile && isProfileImageMarkedForRemoval,
      });

      await refetchSession();
      await queryClient.invalidateQueries({ queryKey: ['profile'] });
      toast.success(t('messages.saveSuccess'));
      resetFormState();
      closeModal();
    } catch {
      const uploadFailedMessage = t('profile.image.errors.uploadFailed');
      setProfileImageError(uploadFailedMessage);
      toast.error(uploadFailedMessage);
    }
  };

  return (
    <Dialog
      open={isOpen}
      onOpenChange={(open) => {
        if (!open) {
          resetFormState();
          closeModal();
        }
      }}
    >
      <DialogContent className="w-11/12 sm:max-w-xl">
        <DialogHeader>
          <div className="flex justify-between">
            <div className="flex flex-col gap-2">
              <DialogTitle>{t('title')}</DialogTitle>
              <DialogDescription>{t('description')}</DialogDescription>
            </div>

            <DialogClose />
          </div>
        </DialogHeader>

        <div className="grow flex flex-col gap-5">
          <div className="flex flex-col gap-2">
            <Label>{t('profile.image.label')}</Label>

            <div className="flex gap-4">
              <Avatar className="h-20 w-20">
                <AvatarImage src={profileImageSrc ?? undefined} />
                <AvatarFallback>{name[0]}</AvatarFallback>
              </Avatar>

              <div className="flex flex-1 flex-col gap-2">
                <input
                  ref={fileInputRef}
                  type="file"
                  className="hidden"
                  accept={PROFILE_IMAGE_ALLOWED_TYPES.join(',')}
                  onChange={onSelectProfileImageFile}
                />

                <div className="flex gap-2">
                  <Button
                    type="button"
                    variant="outline"
                    disabled={isSaving}
                    onClick={() => fileInputRef.current?.click()}
                  >
                    {t('profile.image.actions.select')}
                  </Button>

                  <Button
                    type="button"
                    variant="ghost"
                    disabled={
                      (!selectedProfileImageFile && !hasExistingProfileImage) ||
                      isSaving
                    }
                    onClick={onClickProfileImageAction}
                  >
                    {selectedProfileImageFile
                      ? t('profile.image.actions.clearSelection')
                      : isProfileImageMarkedForRemoval
                        ? t('profile.image.actions.undoRemove')
                        : t('profile.image.actions.remove')}
                  </Button>
                </div>

                <p className="text-sm text-muted-foreground">
                  {t('profile.image.helpText')}
                </p>

                {selectedProfileImageFile && (
                  <p className="text-sm text-muted-foreground">
                    {selectedProfileImageFile.name}
                  </p>
                )}

                {profileImageError && (
                  <p className="text-sm text-destructive">
                    {profileImageError}
                  </p>
                )}
              </div>
            </div>
          </div>

          <div className="flex flex-col gap-2">
            <Label>{t('profile.name.label')}</Label>
            <Input
              type="text"
              value={name}
              onChange={(e) => setEditedName(e.target.value)}
            />
          </div>
        </div>

        <DialogFooter>
          <div className="flex gap-2">
            <Button
              type="button"
              variant="outline"
              disabled={isSaving}
              onClick={() => {
                resetFormState();
                closeModal();
              }}
            >
              {t('actions.cancel')}
            </Button>
            <Button type="button" disabled={isSaving} onClick={onSave}>
              {t('actions.save')}
            </Button>
          </div>
        </DialogFooter>
      </DialogContent>
    </Dialog>
  );
}
