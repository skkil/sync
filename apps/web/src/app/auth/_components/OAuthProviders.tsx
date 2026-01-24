'use client';

import { GoogleLogoIcon } from '@phosphor-icons/react';
import { redirect } from 'next/navigation';

import { Button } from '@/components/ui/button';
import { env } from '@/lib/env';

type OAuthProvider = 'google';

const providers: {
  id: OAuthProvider;
  icon: React.ReactNode;
}[] = [
  {
    id: 'google',
    icon: <GoogleLogoIcon />,
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
          onClick={() => {
            redirect(
              `${env.NEXT_PUBLIC_BACKEND_URL}/oauth2/authorization/${provider.id}`,
            );
          }}
        >
          {provider.icon}
        </Button>
      ))}
    </div>
  );
}
