export type OAuth2Provider = 'GOOGLE';

export interface Profile {
  id: string;
  email: string;
  name: string;
  bio: string | null;
  profession: string | null;
}
