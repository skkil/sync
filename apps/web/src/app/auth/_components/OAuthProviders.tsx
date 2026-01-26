'use client';

import {
  AppleLogoIcon,
  GoogleLogoIcon,
  LinkedinLogoIcon,
} from '@phosphor-icons/react';

import { Button } from '@/components/ui/button';

const providers: {
  id: string;
  icon: React.ReactNode;
}[] = [
  {
    id: 'google',
    icon: <GoogleLogoIcon />,
  },
  {
    id: 'apple',
    icon: <AppleLogoIcon />,
  },
  {
    id: 'linkedin',
    icon: <LinkedinLogoIcon />,
  },
];
export default function OAuthProviders() {
  return (
    <div className="flex gap-4 w-full justify-center items-center">
      {providers.map((provider) => (
        <Button
          key={provider.id}
          variant="outline"
          size="icon"
          onClick={() => {}}
        >
          {provider.icon}
        </Button>
      ))}
    </div>
  );
}
