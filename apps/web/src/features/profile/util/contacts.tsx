import {
  GithubLogoIcon,
  InstagramLogoIcon,
  LinkedinLogoIcon,
  StarIcon,
  XLogoIcon,
} from '@phosphor-icons/react';

import { ContactType } from '@/types/profile';

export const ContactFields: {
  id: ContactType;
  icon: React.ReactNode;
  prefix: string;
}[] = [
  {
    id: 'custom',
    icon: <StarIcon />,
    prefix: '',
  },
  {
    id: 'linkedin',
    icon: <LinkedinLogoIcon />,
    prefix: 'https://linkedin.com/in/',
  },
  {
    id: 'github',
    icon: <GithubLogoIcon />,
    prefix: 'https://github.com/',
  },
  {
    id: 'instagram',
    icon: <InstagramLogoIcon />,
    prefix: 'https://instagram.com/',
  },
  {
    id: 'twitter',
    icon: <XLogoIcon />,
    prefix: 'https://twitter.com/',
  },
];
