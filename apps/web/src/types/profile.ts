export interface Contact {
  id: string;
  type: 'email' | 'phone';
  value: string;
}

export interface Profile {
  id: string;
  name: string;
  bio: string;
  contacts: Contact[];
}
