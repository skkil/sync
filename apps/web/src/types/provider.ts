export enum ProviderType {
  COMPANY = 'COMPANY',
  SCHOOL = 'SCHOOL',
  LAB = 'LAB',
  CONTEST = 'CONTEST',
}

export enum SchoolType {
  UNIVERSITY = 'UNIVERSITY',
  HIGH_SCHOOL = 'HIGH_SCHOOL',
}

export type Provider = {
  id: string;
  name: string;
} & (
  | {
      type: ProviderType.COMPANY;
      industry: string;
    }
  | {
      type: ProviderType.SCHOOL;
    }
  | {
      type: ProviderType.CONTEST;
    }
);
