'use client';

import { EnvelopeIcon } from '@phosphor-icons/react';
import { useTranslations } from 'next-intl';
import Link from 'next/link';
import { notFound } from 'next/navigation';
import { ComponentType } from 'react';

import { Avatar, AvatarFallback } from '@/components/ui/avatar';
import { Button } from '@/components/ui/button';
import { Card, CardContent, CardHeader, CardTitle } from '@/components/ui/card';
import { Separator } from '@/components/ui/separator';
import { useGetExperiencesQuery } from '@/features/experience/api/get-experiences';
import { useGetProfileQuery } from '@/features/profile/api/get-profile';
import { useSession } from '@/lib/auth/client';
import { Experience, ExperienceType } from '@/types/experience';

import AddExperienceButton from './AddExperienceButton';
import EducationExperience from './EducationExperience';
import EmploymentExperience from './EmploymentExperience';
import FollowButton from './FollowButton';

interface ProfileProps {
  userId: string;
}

const categories: {
  id: string;
  type: ExperienceType;
  render: ComponentType<{
    userId: string;
    experience: Experience;
  }>;
}[] = [
  {
    id: 'employment',
    type: ExperienceType.EMPLOYMENT,
    render: EmploymentExperience,
  },
  {
    id: 'education',
    type: ExperienceType.EDUCATION,
    render: EducationExperience,
  },
];

export default function Profile({ userId }: ProfileProps) {
  const t = useTranslations('pages.profile');

  const { data: session, isPending } = useSession();
  const { data: profile, isError } = useGetProfileQuery(userId);

  const { data: experiences } = useGetExperiencesQuery(userId);

  if (isError) {
    notFound();
  }

  return (
    <div className="flex flex-col gap-4 my-4">
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

              <div className="h-9">
                {!isPending && session?.user.id !== profile.id && (
                  <div className="flex gap-2">
                    <FollowButton userId={userId} />
                    <Link href={`/messages?to=${profile.id}`}>
                      <Button variant="outline">{t('header.message')}</Button>
                    </Link>
                  </div>
                )}
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

      {categories.map(({ id, type, render: Component }) => {
        return (
          <Card key={id} className="mx-auto w-full max-w-3xl">
            <CardHeader>
              <CardTitle className="flex justify-between items-center">
                <h2 className="text-xl font-bold">{t(`${id}.title`)}</h2>
                {session?.user.id === userId && (
                  <AddExperienceButton type={type} />
                )}
              </CardTitle>
            </CardHeader>

            <CardContent>
              {experiences &&
                experiences
                  .filter((experience) => experience.type === type)
                  .map((experience, index) => (
                    <div key={experience.id}>
                      <Component userId={userId} experience={experience} />

                      {index ===
                      experiences.filter(
                        (experience) => experience.type === type,
                      ).length -
                        1 ? null : (
                        <Separator className="my-4" />
                      )}
                    </div>
                  ))}
            </CardContent>
          </Card>
        );
      })}
    </div>
  );
}
