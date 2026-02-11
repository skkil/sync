import { Experience, ExperienceType } from '@/types/experience';

import ExperienceWrapper from './ExperienceWrapper';

interface EducationExperienceProps {
  userId: string;
  experience: Experience;
}

export default function EducationExperience({
  userId,
  experience: education,
}: EducationExperienceProps) {
  if (education.type !== ExperienceType.EDUCATION) {
    return null;
  }

  return (
    <ExperienceWrapper userId={userId} experience={education}>
      <h3 className="font-semibold">{education.provider.name}</h3>
      {education.major && <p>{education.major}</p>}
      {education.startDate.getFullYear()} - {education.endDate.getFullYear()}
    </ExperienceWrapper>
  );
}
