'use client';

import { useParams } from 'next/navigation';

import { useGetProjectExperiences } from '@/api/__generated__/experiences/experiences';

export default function ProfileProjects() {
  const { handle } = useParams();
  const { data } = useGetProjectExperiences(handle?.toString() || '');

  if (!data) {
    return null;
  }

  const experiences = data.data.experiences;

  return experiences.map((experience) => (
    <div key={experience.id}>
      <h2>{experience.projectName}</h2>
    </div>
  ));
}
