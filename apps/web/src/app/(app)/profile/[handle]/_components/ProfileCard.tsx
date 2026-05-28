'use client';
import { zodResolver } from '@hookform/resolvers/zod';
import { EnvelopeIcon, PencilIcon } from '@phosphor-icons/react';
import { useQueryClient } from '@tanstack/react-query';
import { useTranslations } from 'next-intl';
import Link from 'next/link';
import { notFound } from 'next/navigation';
import { useEffect, useMemo, useState } from 'react';
import { Controller, useForm } from 'react-hook-form';
import { toast } from 'sonner';
import z from 'zod';

import { useUploadMedia } from '@/api/__generated__/media/media';
import {
  getGetAuthenticatedUserQueryOptions,
  getGetProfileByHandleQueryOptions,
  useGetAuthenticatedUser,
  useGetProfileByHandle,
  useUpdateProfile,
} from '@/api/__generated__/profile/profile';
import { uploadFileToS3 } from '@/api/s3';
import { Avatar, AvatarFallback, AvatarImage } from '@/components/ui/avatar';
import { Button } from '@/components/ui/button';
import { Card, CardContent, CardHeader } from '@/components/ui/card';
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
import { Skeleton } from '@/components/ui/skeleton';
import { Textarea } from '@/components/ui/textarea';
import { ContactFields } from '@/features/profile/util/contacts';
import { useFollowUserMutation } from '@/features/user/api/follow-user';
import { useUnfollowUserMutation } from '@/features/user/api/unfollow-user';
import { useSession } from '@/lib/auth/client';
import SyncError, { ErrorCode } from '@/lib/error';

interface ProfileOverviewProps {
  handle: string;
}

export default function ProfileOverview({ handle }: ProfileOverviewProps) {
  const t = useTranslations('pages.profile');

  const {
    data: profile,
    isPending,
    error,
    isError,
  } = useGetProfileByHandle(handle);

  useEffect(() => {
    if (isError && error instanceof SyncError) {
      switch (error.code) {
        case ErrorCode.USER_NOT_FOUND:
          notFound();
      }
    }
  }, [error, isError]);

  if (!profile) {
    return <Skeleton className="min-h-96 p-0" />;
  }

  return (
    <Card className="min-h-96 p-0">
      <CardHeader className="relative h-48 bg-muted">
        <Avatar className="h-32 w-32 absolute left-8 -bottom-16">
          <AvatarImage src={profile.data.profileImageUrl ?? undefined} />
          <AvatarFallback></AvatarFallback>
        </Avatar>
      </CardHeader>

      {profile && (
        <CardContent className="my-10 mx-4">
          <div className="flex justify-between items-center">
            <div className="flex flex-col gap-1">
              <h2 className="text-3xl font-bold mt-3">{profile.data.name}</h2>
              <p>{profile.data.profession}</p>
            </div>

            <div className="h-9">
              {!isPending && (
                <div className="flex gap-2">
                  {profile.data.isAuthenticatedUser ? (
                    <EditProfileDialog />
                  ) : (
                    <>
                      <FollowButton handle={handle} />
                      <Link href={`/messages?to=${handle}`}>
                        <Button variant="outline">{t('header.message')}</Button>
                      </Link>
                    </>
                  )}
                </div>
              )}
            </div>
          </div>

          <div className="flex flex-col-reverse md:flex-row md:justify-between mt-2 gap-4">
            {profile.data.bio && (
              <div className="border-black border-l-2 p-2 w-96 text-pretty break-words">
                {profile.data.bio}
              </div>
            )}

            <div className="flex flex-col gap-2">
              <div className="flex flex-col">
                <div className="text-sm text-muted-foreground flex items-center gap-2">
                  <p>
                    <EnvelopeIcon />
                  </p>
                  <p>{profile.data.email}</p>
                </div>

                <ProfileContacts handle={handle} />
              </div>
            </div>
          </div>
        </CardContent>
      )}
    </Card>
  );
}

function ProfileContacts({ handle }: { handle: string }) {
  const t = useTranslations('pages.profile');

  const [showAllContacts, setShowAllContacts] = useState(false);

  const { data: profile } = useGetProfileByHandle(handle);

  if (!profile || !profile.data.contacts) {
    return null;
  }

  const contacts = profile.data.contacts;

  const filteredFields = ContactFields.filter((field) => {
    const contact = contacts[field.id as keyof typeof profile.data.contacts];
    return contact && contact.trim() !== '';
  });

  const displayFields = showAllContacts
    ? filteredFields
    : filteredFields.slice(0, 2);

  return (
    <>
      {displayFields.map((field) => {
        const contact =
          contacts[field.id as keyof typeof profile.data.contacts];

        return (
          <Link
            key={field.id}
            className="text-sm text-muted-foreground flex items-center gap-2"
            href={`${field.prefix}${contact}`}
            target="_blank"
            rel="noopener noreferrer"
          >
            <p>{field.icon}</p>
            <p>
              {field.prefix}
              {contact}
            </p>
          </Link>
        );
      })}

      {filteredFields.length > 2 && (
        <Button
          variant="ghost"
          size="sm"
          onClick={() => setShowAllContacts(!showAllContacts)}
          className="w-fit text-sm"
        >
          {showAllContacts
            ? t('header.contacts.show-less')
            : `${t('header.contacts.show-more')} (${filteredFields.length - 2})`}
        </Button>
      )}
    </>
  );
}

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

function EditProfileDialog() {
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

          await context.client.invalidateQueries(
            getGetAuthenticatedUserQueryOptions(),
          );

          await context.client.invalidateQueries(
            getGetProfileByHandleQueryOptions(profile?.data.handle || ''),
          );

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

        await context.client.invalidateQueries(
          getGetAuthenticatedUserQueryOptions(),
        );

        await context.client.invalidateQueries(
          getGetProfileByHandleQueryOptions(profile?.data.handle || ''),
        );
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
      },
    });

    const { success: uploadSuccess } = await uploadFileToS3({
      file,
      uploadUrl,
    });

    if (!uploadSuccess) {
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

interface FollowButtonProps {
  handle: string;
}

function FollowButton({ handle }: FollowButtonProps) {
  const t = useTranslations('pages.profile.header');
  const queryClient = useQueryClient();

  const { data: session } = useSession();
  const { data: profile, isPending } = useGetProfileByHandle(handle);

  const invalidateProfile = () => {
    void queryClient.invalidateQueries(getGetProfileByHandleQueryOptions(handle));
  };

  const { mutate: followUser, isPending: isFollowPending } =
    useFollowUserMutation();
  const { mutate: unfollowUser, isPending: isUnfollowPending } =
    useUnfollowUserMutation();

  if (isPending || !profile) {
    return null;
  }

  if (session?.user.id === profile.data.userId) {
    return null;
  }

  if (profile.data.isFollowing) {
    return (
      <Button
        disabled={isUnfollowPending}
        onClick={() => {
          unfollowUser(profile.data.userId, {
            onSuccess: invalidateProfile,
          });
        }}
      >
        {t('unfollow')}
      </Button>
    );
  } else {
    return (
      <Button
        disabled={isFollowPending}
        onClick={() => {
          followUser(profile.data.userId, {
            onSuccess: invalidateProfile,
          });
        }}
      >
        {t('follow')}
      </Button>
    );
  }
}
