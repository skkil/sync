'use client';

import _ from 'lodash';
import { useTranslations } from 'next-intl';
import { notFound, useParams } from 'next/navigation';
import { ComponentType } from 'react';

import { useGetProfile } from '@/api/__generated__/profile/profile';
import { Card, CardContent, CardHeader, CardTitle } from '@/components/ui/card';
import { Separator } from '@/components/ui/separator';
import { useGetExperiencesQuery } from '@/features/experience/api/get-experiences';
import { Experience, ExperienceType } from '@/types/experience';

import AddExperienceButton from './_components/AddExperienceButton';
import EducationExperience from './_components/EducationExperience';
import EmploymentExperience from './_components/EmploymentExperience';

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

export default function ProfileResume() {
  const t = useTranslations('pages.profile');

  const { handle } = useParams();

  const { data: profile, isError } = useGetProfile(handle?.toString() || '');

  const { data: experiences } = useGetExperiencesQuery(
    profile?.data.userId || '',
  );

  if (isError) {
    notFound();
  }

  const experiencesByType = _.groupBy(experiences, 'type');

  return (
    <div className="flex flex-col gap-4 my-4">
      {categories.map(({ id, type, render: Component }) => {
        return (
          <Card key={id} className="mx-auto w-full max-w-3xl">
            <CardHeader>
              <CardTitle className="flex justify-between items-center">
                <h2 className="text-xl font-bold">{t(`${id}.title`)}</h2>
                {profile?.data.isAuthenticatedUser && (
                  <AddExperienceButton type={type} />
                )}
              </CardTitle>
            </CardHeader>

            <CardContent>
              {experiencesByType[type]?.map((experience, index) => (
                <div key={experience.id}>
                  {profile && (
                    <Component
                      userId={profile.data.userId}
                      experience={experience}
                    />
                  )}

                  {experiencesByType[type] &&
                    index < experiencesByType[type]?.length - 1 && (
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
