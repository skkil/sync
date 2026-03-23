import { Experience, ExperienceType } from '@/types/experience';

import ExperienceWrapper from './ExperienceWrapper';

interface EmploymentExperienceProps {
  userId: string;
  experience: Experience;
}

export default function EmploymentExperience({
  userId,
  experience: employment,
}: EmploymentExperienceProps) {
  if (employment.type !== ExperienceType.EMPLOYMENT) {
    return null;
  }

  return (
    <ExperienceWrapper userId={userId} experience={employment}>
      <h3 className="font-semibold">{employment.provider.name}</h3>
      {employment.startDate.getFullYear()} -{' '}
      {employment.endDate ? employment.endDate.getFullYear() : ''}
    </ExperienceWrapper>
  );
}
