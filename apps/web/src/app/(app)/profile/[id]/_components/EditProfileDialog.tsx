import { zodResolver } from '@hookform/resolvers/zod';
import { PencilIcon } from '@phosphor-icons/react';
import { useTranslations } from 'next-intl';
import { ChangeEvent, useEffect, useMemo, useRef, useState } from 'react';
import { Controller, useForm } from 'react-hook-form';
import { toast } from 'sonner';
import z from 'zod';

import { Avatar, AvatarFallback, AvatarImage } from '@/components/ui/avatar';
import { Button } from '@/components/ui/button';
import {
  Dialog,
  DialogClose,
  DialogContent,
  DialogDescription,
  DialogFooter,
  DialogHeader,
  DialogTitle,
  DialogTrigger,
} from '@/components/ui/dialog';
import {
  Field,
  FieldError,
  FieldGroup,
  FieldLabel,
} from '@/components/ui/field';
import { Input } from '@/components/ui/input';
import {
  InputGroup,
  InputGroupAddon,
  InputGroupInput,
} from '@/components/ui/input-group';
import { ScrollArea } from '@/components/ui/scroll-area';
import { Textarea } from '@/components/ui/textarea';
import {
  PROFILE_IMAGE_ALLOWED_TYPES,
  PROFILE_IMAGE_MAX_SIZE_BYTES,
} from '@/features/media/api/upload-profile-image';
import { useGetProfileQuery } from '@/features/profile/api/get-profile';
import { useUpdateProfileWithImageMutation } from '@/features/profile/api/update-profile';
import { ContactFields } from '@/features/profile/util/contacts';
import { useSession } from '@/lib/auth/client';
import SyncError, { ErrorCode } from '@/lib/error';

const EditProfileFormSchema = (t: ReturnType<typeof useTranslations>) =>
  z.object({
    name: z.string().min(1, t('form.errors.required_name')),
    profession: z.string(),
    bio: z.string(),
    contacts: z.object({
      custom: z.string(),
      linkedin: z.string(),
      github: z.string(),
      instagram: z.string(),
      twitter: z.string(),
    }),
  });

export default function EditProfileDialog() {
  const t = useTranslations('pages.profile.edit');
  const tSettings = useTranslations('modals.settings');

  const { data: session, refetch: refetchSession } = useSession();
  const { data: profile } = useGetProfileQuery(session?.user.id || '');

  const { mutate: updateProfileWithImage, isPending: isSaving } =
    useUpdateProfileWithImageMutation();

  const schema = useMemo(() => EditProfileFormSchema(t), [t]);
  type EditProfileFormValues = z.infer<typeof schema>;

  const form = useForm<EditProfileFormValues>({
    resolver: zodResolver(schema),
    defaultValues: {
      name: '',
      profession: '',
      bio: '',
      contacts: {
        custom: '',
        linkedin: '',
        github: '',
        instagram: '',
        twitter: '',
      },
    },
  });

  const [open, setOpen] = useState(false);
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

  useEffect(() => {
    if (profile) {
      form.reset({
        name: profile.name || '',
        profession: profile.profession || '',
        bio: profile.bio || '',
        contacts: {
          custom: profile.contacts?.custom || '',
          linkedin: profile.contacts?.linkedin || '',
          github: profile.contacts?.github || '',
          instagram: profile.contacts?.instagram || '',
          twitter: profile.contacts?.twitter || '',
        },
      });
    }
  }, [form, profile]);

  useEffect(() => {
    return () => {
      if (profileImagePreviewUrl) {
        URL.revokeObjectURL(profileImagePreviewUrl);
      }
    };
  }, [profileImagePreviewUrl]);

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

  const resetDialogState = () => {
    setIsProfileImageMarkedForRemoval(false);
    clearSelectedProfileImage();
  };

  const onSelectProfileImageFile = (event: ChangeEvent<HTMLInputElement>) => {
    const file = event.target.files?.[0];
    if (!file) {
      return;
    }

    if (!PROFILE_IMAGE_ALLOWED_TYPES.includes(file.type as never)) {
      setProfileImageError(tSettings('profile.image.errors.unsupportedType'));
      event.target.value = '';
      return;
    }

    if (file.size > PROFILE_IMAGE_MAX_SIZE_BYTES) {
      setProfileImageError(tSettings('profile.image.errors.maxSize'));
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

    if (profile?.profileImageUrl) {
      setIsProfileImageMarkedForRemoval((prev) => !prev);
      setProfileImageError(null);
    }
  };

  const formSubmitHandler = (values: EditProfileFormValues) => {
    updateProfileWithImage(
      {
        ...values,
        profileImageFile: selectedProfileImageFile,
        removeProfileImage:
          !selectedProfileImageFile && isProfileImageMarkedForRemoval,
      },
      {
        onSuccess: () => {
          resetDialogState();
          setOpen(false);
          refetchSession();
          toast.success(tSettings('messages.saveSuccess'));
        },
        onError: (error) => {
          if (
            error instanceof SyncError &&
            error.code === ErrorCode.MEDIA_UPLOAD_FAILED
          ) {
            const uploadFailedMessage = tSettings(
              'profile.image.errors.uploadFailed',
            );
            setProfileImageError(uploadFailedMessage);
            toast.error(uploadFailedMessage);
            return;
          }

          if (error instanceof Error) {
            toast.error(error.message);
          } else {
            const uploadFailedMessage = tSettings(
              'profile.image.errors.uploadFailed',
            );
            setProfileImageError(uploadFailedMessage);
            toast.error(uploadFailedMessage);
          }
        },
      },
    );
  };

  const profileImageSrc =
    profileImagePreviewUrl ??
    (isProfileImageMarkedForRemoval
      ? null
      : (profile?.profileImageUrl ?? null));
  const currentName = form.watch('name');

  return (
    <Dialog
      open={open}
      onOpenChange={(nextOpen) => {
        if (!nextOpen) {
          resetDialogState();
        }
        setOpen(nextOpen);
      }}
    >
      <DialogTrigger asChild>
        <Button variant="ghost" aria-label={t('title')}>
          <PencilIcon />
        </Button>
      </DialogTrigger>

      <DialogContent className="min-w-5xl">
        <DialogHeader>
          <div className="flex items-center justify-between">
            <div className="flex flex-col gap-2">
              <DialogTitle>{t('title')}</DialogTitle>
              <DialogDescription>{t('description')}</DialogDescription>
            </div>

            <DialogClose />
          </div>
        </DialogHeader>

        <form onSubmit={form.handleSubmit(formSubmitHandler)}>
          <ScrollArea className="min-h-120">
            <h2 className="text-lg font-bold">{t('form.groups.basic')}</h2>
            <FieldGroup className="p-3">
              <Field>
                <FieldLabel>{tSettings('profile.image.label')}</FieldLabel>

                <div className="mt-2 flex gap-4">
                  <Avatar className="h-20 w-20">
                    <AvatarImage src={profileImageSrc ?? undefined} />
                    <AvatarFallback>{currentName?.[0] ?? '?'}</AvatarFallback>
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
                        {tSettings('profile.image.actions.select')}
                      </Button>

                      <Button
                        type="button"
                        variant="ghost"
                        disabled={
                          (!selectedProfileImageFile &&
                            !profile?.profileImageUrl) ||
                          isSaving
                        }
                        onClick={onClickProfileImageAction}
                      >
                        {selectedProfileImageFile
                          ? tSettings('profile.image.actions.clearSelection')
                          : isProfileImageMarkedForRemoval
                            ? tSettings('profile.image.actions.undoRemove')
                            : tSettings('profile.image.actions.remove')}
                      </Button>
                    </div>

                    <p className="text-sm text-muted-foreground">
                      {tSettings('profile.image.helpText')}
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
              </Field>

              <Controller
                name="name"
                control={form.control}
                render={({ field, fieldState }) => (
                  <Field data-invalid={fieldState.invalid}>
                    <div className="flex items-center justify-between">
                      <FieldLabel>{t('form.fields.name.label')}</FieldLabel>

                      {fieldState.invalid && (
                        <FieldError errors={[fieldState.error]} />
                      )}
                    </div>

                    <Input
                      {...field}
                      aria-invalid={fieldState.invalid}
                      placeholder={t('form.fields.name.placeholder')}
                      autoComplete="name"
                    />
                  </Field>
                )}
              />

              <Controller
                name="profession"
                control={form.control}
                render={({ field, fieldState }) => (
                  <Field data-invalid={fieldState.invalid}>
                    <div className="flex items-center justify-between">
                      <FieldLabel>
                        {t('form.fields.profession.label')}
                      </FieldLabel>

                      {fieldState.invalid && (
                        <FieldError errors={[fieldState.error]} />
                      )}
                    </div>

                    <Input
                      {...field}
                      aria-invalid={fieldState.invalid}
                      placeholder={t('form.fields.profession.placeholder')}
                    />
                  </Field>
                )}
              />

              <Controller
                name="bio"
                control={form.control}
                render={({ field, fieldState }) => (
                  <Field data-invalid={fieldState.invalid}>
                    <div className="flex items-center justify-between">
                      <FieldLabel>{t('form.fields.bio.label')}</FieldLabel>

                      {fieldState.invalid && (
                        <FieldError errors={[fieldState.error]} />
                      )}
                    </div>

                    <Textarea
                      {...field}
                      aria-invalid={fieldState.invalid}
                      placeholder={t('form.fields.bio.placeholder')}
                    />
                  </Field>
                )}
              />
            </FieldGroup>

            <h2 className="text-lg font-bold">{t('form.groups.contacts')}</h2>
            <FieldGroup className="p-3">
              {ContactFields.map((contactField) => (
                <Controller
                  key={contactField.id}
                  name={`contacts.${contactField.id}`}
                  control={form.control}
                  render={({ field, fieldState }) => (
                    <Field data-invalid={fieldState.invalid}>
                      <InputGroup>
                        <InputGroupAddon>{contactField.icon}</InputGroupAddon>
                        {contactField.prefix && (
                          <InputGroupAddon className="text-muted-foreground">
                            {contactField.prefix}
                          </InputGroupAddon>
                        )}
                        <InputGroupInput {...field} />
                      </InputGroup>
                    </Field>
                  )}
                />
              ))}
            </FieldGroup>
          </ScrollArea>

          <DialogFooter>
            <Button isPending={isSaving} type="submit">
              {t('form.actions.submit.label')}
            </Button>
          </DialogFooter>
        </form>
      </DialogContent>
    </Dialog>
  );
}
