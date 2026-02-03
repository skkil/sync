import { Experience, ExperienceType } from '@/types/experience';

interface EducationExperienceProps {
  experience: Experience;
}

export default function EducationExperience({
  experience: education,
}: EducationExperienceProps) {
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
