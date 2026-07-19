'use client';
import { zodResolver } from '@hookform/resolvers/zod';
import { EnvelopeIcon, PencilIcon } from '@phosphor-icons/react';
import { useTranslations } from 'next-intl';
import Link from 'next/link';
import { notFound } from 'next/navigation';
import { useEffect, useMemo, useState } from 'react';
import { Controller, useForm } from 'react-hook-form';
import { toast } from 'sonner';
import z from 'zod';

import {
  useGetAuthenticatedUser,
  useGetProfileByHandle,
} from '@/api/__generated__/profile/profile';
import { FollowButton } from '@/components/feature/profile/FollowButton';
import { ContactFields } from '@/components/feature/profile/contacts';
import { useProfileImageUpload } from '@/components/feature/profile/hooks/useProfileImageUpload';
import { useUpdateProfile } from '@/components/feature/profile/hooks/useUpdateProfile';
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
import { FileInput, Input } from '@/components/ui/input';
import {
  InputGroup,
  InputGroupAddon,
  InputGroupInput,
} from '@/components/ui/input-group';
import { ScrollArea } from '@/components/ui/scroll-area';
import { Skeleton } from '@/components/ui/skeleton';
import { Textarea } from '@/components/ui/textarea';
import { useSession } from '@/lib/auth/client';
import SyncError, { ErrorCode } from '@/lib/error';
import ROUTES from '@/util/routes';

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
    return <Skeleton className="h-56 w-full" />;
  }

  return (
    <section className="flex flex-col gap-6">
      <div className="flex flex-col gap-6 sm:flex-row sm:items-start">
        <Avatar className="h-28 w-28 border">
          <AvatarImage src={profile.data.profileImageUrl ?? undefined} />
          <AvatarFallback></AvatarFallback>
        </Avatar>

        <div className="flex flex-1 flex-col gap-4">
          <div className="flex flex-col gap-4 sm:flex-row sm:items-center sm:justify-between">
            <div className="flex flex-col gap-1">
              <h2 className="text-2xl font-semibold tracking-tight">
                {profile.data.name}
              </h2>
              {profile.data.profession && (
                <p className="text-muted-foreground">
                  {profile.data.profession}
                </p>
              )}
            </div>

            <div className="h-9">
              {!isPending && (
                <div className="flex gap-2">
                  {profile.data.isAuthenticatedUser ? (
                    <EditProfileDialog />
                  ) : (
                    <>
                      <FollowButton handle={handle} />
                      <Link href={ROUTES.MESSAGES(handle)}>
                        <Button variant="outline">{t('header.message')}</Button>
                      </Link>
                    </>
                  )}
                </div>
              )}
            </div>
          </div>

          <div className="flex items-center gap-4 text-sm">
            <Link
              href={ROUTES.PROFILE_FOLLOWERS(handle)}
              className="hover:underline"
            >
              <span className="font-semibold">
                {profile.data.followerCount}
              </span>{' '}
              <span className="text-muted-foreground">
                {t('header.followers')}
              </span>
            </Link>
            <Link
              href={ROUTES.PROFILE_FOLLOWING(handle)}
              className="hover:underline"
            >
              <span className="font-semibold">
                {profile.data.followingCount}
              </span>{' '}
              <span className="text-muted-foreground">
                {t('header.following')}
              </span>
            </Link>
          </div>

          {profile.data.bio && (
            <p className="max-w-2xl text-pretty break-words leading-relaxed text-foreground/90">
              {profile.data.bio}
            </p>
          )}

          <div className="flex flex-wrap items-center gap-x-6 gap-y-2 text-sm text-muted-foreground">
            <div className="flex items-center gap-2">
              <EnvelopeIcon />
              <p>{profile.data.email}</p>
            </div>

            <ProfileContacts handle={handle} />
          </div>
        </div>
      </div>
    </section>
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
      handle: profile?.data.handle || '',
      onSuccess: async () => {
        await refetchSession();
        toast.success(t('messages.success'));
        closeDialog();
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
  const tCommon = useTranslations();

  const { data: profile } = useGetAuthenticatedUser();

  const {
    selectedImage,
    error,
    isUploadMediaPending,
    handleFileChange,
    handleFileError,
    handleRemoveImage,
  } = useProfileImageUpload({
    handle: profile?.data.handle || '',
    onUploadSuccess: () => toast.success(t('messages.success')),
  });

  if (!profile) {
    return null;
  }

  const errorMessage = error
    ? error.code === 'maxSize'
      ? t('errors.maxSize')
      : error.code === 'unsupportedType'
        ? t('errors.unsupportedType')
        : error.code === 'network'
          ? tCommon('errors.connection-failed')
          : error.code === 'custom'
            ? error.message
            : t('errors.uploadFailed')
    : null;

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

      {errorMessage && <div className="text-destructive">{errorMessage}</div>}
    </Field>
  );
}
