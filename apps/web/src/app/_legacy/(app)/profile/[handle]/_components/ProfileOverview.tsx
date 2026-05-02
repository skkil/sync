'use client';

import { EnvelopeIcon } from '@phosphor-icons/react';
import { useTranslations } from 'next-intl';
import Link from 'next/link';
import { notFound } from 'next/navigation';
import { useEffect, useState } from 'react';

import { useGetProfileByHandle } from '@/api/__generated__/profile/profile';
import { Avatar, AvatarFallback, AvatarImage } from '@/components/ui/avatar';
import { Button } from '@/components/ui/button';
import { Card, CardContent, CardHeader } from '@/components/ui/card';
import { Skeleton } from '@/components/ui/skeleton';
import { ContactFields } from '@/features/profile/util/contacts';
import SyncError, { ErrorCode } from '@/lib/error';

import EditProfileDialog from './EditProfileDialog';
import FollowButton from './FollowButton';

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
