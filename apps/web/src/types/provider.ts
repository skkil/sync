export enum ProviderType {
  COMPANY = 'COMPANY',
  SCHOOL = 'SCHOOL',
  LAB = 'LAB',
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
);
