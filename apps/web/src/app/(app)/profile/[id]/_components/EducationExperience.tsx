import { Experience, ExperienceType } from '@/types/experience';

interface EmploymentExperienceProps {
  experience: Experience;
}

export default function EmploymentExperience({
  experience: education,
}: EmploymentExperienceProps) {
  if (education.type !== ExperienceType.EDUCATION) {
    return null;
  }

  return (
    <div>
      <h3 className="font-semibold">{education.provider.name}</h3>
      {education.major && <p>{education.major}</p>}
      {education.startDate.getFullYear()} - {education.endDate.getFullYear()}
    </div>
  );
}
