export enum ExperienceType {
  EMPLOYMENT = 'EMPLOYMENT',
  EDUCATION = 'EDUCATION',
}

export type ExperienceVisibility = 'PUBLIC' | 'PRIVATE';

export type Experience = {
  id: string;
  visibility: ExperienceVisibility;
  provider: {
    id: string;
    name: string;
  };
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

export interface Reflection {
  id: string;
  content: string;
  createdAt: Date;
}
