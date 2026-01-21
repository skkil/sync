export interface Contact {
  id: string;
  type: 'email' | 'phone';
  icon: React.ReactNode;
  value: string;
}

export interface Profile {
  id: string;
  name: string;
  bio: string;
  contacts: Contact[];
}
