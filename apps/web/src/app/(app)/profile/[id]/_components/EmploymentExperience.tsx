import { Experience, ExperienceType } from '@/types/experience';

interface EmploymentExperienceProps {
  experience: Experience;
}

export default function EmploymentExperience({
  experience: employment,
}: EmploymentExperienceProps) {
  if (employment.type !== ExperienceType.EMPLOYMENT) {
    return null;
  }

  return (
    <div>
      <h3 className="font-semibold">{employment.provider.name}</h3>
      {employment.startDate.getFullYear()} - {employment.endDate.getFullYear()}
    </div>
  );
}
