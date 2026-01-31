export enum ProviderType {
  COMPANY = 'COMPANY',
  SCHOOL = 'SCHOOL',
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
      category: ProviderType.COMPANY;
      industry: string;
    }
  | {
      category: ProviderType.SCHOOL;
    }
);
