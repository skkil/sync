'use client';

import { EnvelopeIcon } from '@phosphor-icons/react';
import { useTranslations } from 'next-intl';
import { notFound } from 'next/navigation';

import { Avatar, AvatarFallback } from '@/components/ui/avatar';
import { Button } from '@/components/ui/button';
import { Card, CardContent, CardHeader } from '@/components/ui/card';
import { useGetProfile } from '@/features/profile/api/get-profile';
import { Experience, ExperienceType } from '@/types/experience';

interface ProfileProps {
  userId: string;
}

export default function Profile({ userId }: ProfileProps) {
  const t = useTranslations('pages.profile');

  const { data: profile, isError } = useGetProfile(userId);

  const experiences: Experience[] = [];

  if (isError) {
    notFound();
  }

  return (
    <div className="flex flex-col gap-4 mt-4">
      <Card className="mx-auto w-full max-w-3xl min-h-96 p-0">
        <CardHeader className="relative h-48 bg-muted">
          <Avatar className="h-32 w-32 absolute left-8 -bottom-16">
            <AvatarFallback></AvatarFallback>
          </Avatar>
        </CardHeader>

        {profile && (
          <CardContent className="mt-10 mx-4">
            <div className="flex justify-between items-center">
              <h2 className="text-3xl font-bold mt-3">{profile.name}</h2>

              <div className="flex gap-2">
                <Button>{t('header.follow')}</Button>
                <Button variant="outline">{t('header.message')}</Button>
              </div>
            </div>

            <div className="flex flex-col-reverse md:flex-row md:justify-between mt-2 gap-4">
              {profile.bio}
              <div className="flex flex-col">
                <div className="text-sm text-muted-foreground flex items-center gap-2">
                  <p>
                    <EnvelopeIcon />
                  </p>
                  <p>{profile.email}</p>
                </div>
              </div>
            </div>
          </CardContent>
        )}
      </Card>

      <Card className="mx-auto w-full max-w-3xl">
        <CardHeader>
          <h2 className="text-xl font-bold">{t('employment.title')}</h2>
        </CardHeader>

        <CardContent>
          {experiences
            .filter(
              (experience) => experience.type === ExperienceType.EMPLOYMENT,
            )
            .map((employment) => (
              <div key={employment.id}>
                <h3 className="font-semibold">{employment.provider.name}</h3>
                {employment.startDate.getFullYear()} -{' '}
                {employment.endDate.getFullYear()}
              </div>
            ))}
        </CardContent>
      </Card>

      <Card className="mx-auto w-full max-w-3xl">
        <CardHeader>
          <h2 className="text-xl font-bold">{t('education.title')}</h2>
        </CardHeader>

        <CardContent>
          {experiences
            .filter(
              (experience) => experience.type === ExperienceType.EDUCATION,
            )
            .map((education) => (
              <div key={education.id}>
                <h3 className="font-semibold">{education.provider.name}</h3>
                {education.major && <p>{education.major}</p>}
                {education.startDate.getFullYear()} -{' '}
                {education.endDate.getFullYear()}
              </div>
            ))}
        </CardContent>
      </Card>
    </div>
  );
}
