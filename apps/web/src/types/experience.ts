import { Provider } from './provider';

export enum ExperienceCategory {
  EMPLOYMENT = 'employment',
  EDUCATION = 'education',
}

export type Experience = {
  id: string;
  provider: Provider;
  startDate: Date;
  endDate: Date;
} & (
  | {
      category: ExperienceCategory.EMPLOYMENT;
    }
  | {
      category: ExperienceCategory.EDUCATION;
      major?: string;
    }
);
