import { Provider } from './provider';

export enum ExperienceType {
  EMPLOYMENT = 'EMPLOYMENT',
  EDUCATION = 'EDUCATION',
}

export type Experience = {
  id: string;
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
