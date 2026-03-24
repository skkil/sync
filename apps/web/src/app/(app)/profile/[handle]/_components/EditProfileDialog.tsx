import { zodResolver } from '@hookform/resolvers/zod';
import { PencilIcon } from '@phosphor-icons/react';
import { useTranslations } from 'next-intl';
import { useEffect, useMemo, useState } from 'react';
import { Controller, useForm } from 'react-hook-form';
import { toast } from 'sonner';
import z from 'zod';

import { useUploadMedia } from '@/api/__generated__/media/media';
import {
  getGetAuthenticatedUserQueryKey,
  getGetProfileByHandleQueryKey,
  useGetAuthenticatedUser,
  useUpdateProfile,
} from '@/api/__generated__/profile/profile';
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
import { FileInput, FileInputError, Input } from '@/components/ui/input';
import {
  InputGroup,
  InputGroupAddon,
  InputGroupInput,
} from '@/components/ui/input-group';
import { ScrollArea } from '@/components/ui/scroll-area';
import { Textarea } from '@/components/ui/textarea';
import { ContactFields } from '@/features/profile/util/contacts';
import { useSession } from '@/lib/auth/client';

export const PROFILE_IMAGE_ALLOWED_TYPES = 'image/*';
export const PROFILE_IMAGE_MAX_SIZE_BYTES = 5 * 1024 * 1024;

const EditProfileFormSchema = (t: ReturnType<typeof useTranslations>) =>
  z.object({
    name: z.string().min(1, {
      error: t('form.errors.required_name'),
    }),
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

  const { data: session, refetch: refetchSession } = useSession();
  const { data: profile } = useGetAuthenticatedUser();

  const schema = useMemo(() => EditProfileFormSchema(t), [t]);
  type EditProfileFormValues = z.infer<typeof schema>;

  const [open, setOpen] = useState(false);

  const closeDialog = () => {
    form.reset();
    setOpen(false);
  };

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

  const { mutate: updateProfile, isPending: isUpdateProfilePending } =
    useUpdateProfile({
      mutation: {
        onSuccess: async (_data, _variables, _onMutateResult, context) => {
          await refetchSession();

          await context.client.invalidateQueries({
            queryKey: getGetAuthenticatedUserQueryKey(),
          });

          await context.client.invalidateQueries({
            queryKey: getGetProfileByHandleQueryKey(profile?.data.handle || ''),
          });

          toast.success(t('messages.success'));
          closeDialog();
        },
      },
    });

  useEffect(() => {
    if (!profile) {
      return;
    }

    const { name, profession, bio, contacts } = profile.data;
    form.reset({
      name,
      profession,
      bio,
      contacts: {
        custom: contacts?.custom || '',
        linkedin: contacts?.linkedin || '',
        github: contacts?.github || '',
        instagram: contacts?.instagram || '',
        twitter: contacts?.twitter || '',
      },
    });
  }, [form, profile]);

  const formSubmitHandler = async (values: EditProfileFormValues) => {
    updateProfile({
      data: values,
    });
  };

  if (!session || !profile || session.user.id !== profile.data.userId) {
    return null;
  }

  return (
    <Dialog
      open={open}
      onOpenChange={(o) => {
        setOpen(o);
      }}
    >
      <DialogTrigger asChild>
        <Button variant="ghost" aria-label={t('title')}>
          <PencilIcon />
        </Button>
      </DialogTrigger>
      <DialogContent className="w-11/12 sm:max-w-3xl">
        <DialogHeader>
          <div className="flex justify-between">
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
              <ProfileImageField />

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

            <DialogFooter>
              <Button isPending={isUpdateProfilePending} type="submit">
                {t('form.actions.submit.label')}
              </Button>
            </DialogFooter>
          </ScrollArea>
        </form>
      </DialogContent>
    </Dialog>
  );
}

function ProfileImageField() {
  const t = useTranslations('pages.profile.edit.form.image');

  const { refetch: refetchSession } = useSession();
  const { data: profile } = useGetAuthenticatedUser();

  const { mutate: updateProfile } = useUpdateProfile({
    mutation: {
      onSuccess: async (_data, _variables, _onMutateResult, context) => {
        await refetchSession();

        await context.client.invalidateQueries({
          queryKey: getGetAuthenticatedUserQueryKey(),
        });

        await context.client.invalidateQueries({
          queryKey: getGetProfileByHandleQueryKey(profile?.data.handle || ''),
        });
      },
    },
  });

  const { mutateAsync: uploadMedia, isPending: isUploadMediaPending } =
    useUploadMedia();

  const [selectedImage, setSelectedImage] = useState<{
    file: File;
    src: string;
  } | null>(null);
  const [error, setError] = useState<string | null>(null);

  const handleFileError = (error: FileInputError) => {
    if (error === 'size') {
      setError(t('errors.maxSize'));
    } else if (error === 'type') {
      setError(t('errors.unsupportedType'));
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
        mediaContext: 'profile-image',
      },
    });

    const response = await fetch(uploadUrl, {
      method: 'PUT',
      body: file,
      headers: {
        'Content-Type': file.type,
      },
    }).catch(() => null);

    if (!response || !response.ok) {
      toast.error(t('errors.uploadFailed'));
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
          toast.success(t('messages.success'));
          setSelectedImage(null);
        },
        onError: (error) => {
          if (error instanceof Error) {
            setError(error.message);
          } else {
            setError(t('errors.uploadFailed'));
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

  if (!profile) {
    return null;
  }

  return (
    <Field>
      <FieldLabel>{t('label')}</FieldLabel>

      <div className="flex gap-4">
        <Avatar className="h-20 w-20">
          <AvatarImage
            src={
              selectedImage
                ? selectedImage.src
                : profile.data.profileImageUrl || undefined
            }
          />
          <AvatarFallback />
        </Avatar>

        <div className="flex flex-col gap-2">
          <div className="flex">
            <FileInput
              onFileChange={handleFileChange}
              onError={handleFileError}
              accept={PROFILE_IMAGE_ALLOWED_TYPES}
              maxSize={PROFILE_IMAGE_MAX_SIZE_BYTES}
              disabled={isUploadMediaPending}
            >
              <Button type="button">
                {profile.data.profileImageUrl || selectedImage
                  ? t('actions.change')
                  : t('actions.upload')}
              </Button>
            </FileInput>

            {(profile.data.profileImageUrl || selectedImage) && (
              <Button
                type="button"
                variant="destructive"
                onClick={() => handleRemoveImage()}
              >
                {t('actions.remove')}
              </Button>
            )}
          </div>

          <div>
            <div>{t('help.upload')}</div>
            <div>{t('help.public')}</div>
          </div>
        </div>
      </div>

      {error && <div className="text-destructive">{error}</div>}
    </Field>
  );
}
