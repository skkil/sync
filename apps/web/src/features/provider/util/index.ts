import { ExperienceType } from '@/types/experience';
import { ProviderType } from '@/types/provider';

export function getProviderForExperience(type: ExperienceType) {
  switch (type) {
    case ExperienceType.EDUCATION:
      return ProviderType.SCHOOL;

    case ExperienceType.EMPLOYMENT:
    default:
      return ProviderType.COMPANY;
  }
}
