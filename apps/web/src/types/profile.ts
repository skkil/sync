export interface Profile {
  id: string;
  email: string;
  name: string;
  bio: string | null;
  profession: string | null;
  contacts?: {
    custom: string | null;
    linkedin: string | null;
    github: string | null;
    instagram: string | null;
    twitter: string | null;
  };
}

export type ContactType =
  | 'custom'
  | 'linkedin'
  | 'github'
  | 'instagram'
  | 'twitter';
