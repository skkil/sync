import { Provider } from './provider';

export enum ExperienceType {
  EMPLOYMENT = 'EMPLOYMENT',
  EDUCATION = 'EDUCATION',
}

export type ExperienceVisibility = 'PUBLIC' | 'PRIVATE';

export type Experience = {
  id: string;
  visibility: ExperienceVisibility;
  provider: Provider;
  startDate: Date;
  endDate: Date;
} & (
  | {
      type: ExperienceType.EMPLOYMENT;
    }
  | {
      type: ExperienceType.EDUCATION;
      major?: string;
    }
);
