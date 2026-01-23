export enum ProviderCategory {
  COMPANY = 'company',
  SCHOOL = 'school',
}

export type Provider = {
  id: string;
  name: string;
} & (
  | {
      category: ProviderCategory.COMPANY;
      industry: string;
    }
  | {
      category: ProviderCategory.SCHOOL;
    }
);
