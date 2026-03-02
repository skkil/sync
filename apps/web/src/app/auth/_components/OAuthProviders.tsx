'use client';

import { GoogleLogoIcon } from '@phosphor-icons/react';
import { useRouter } from 'next/navigation';

import { Button } from '@/components/ui/button';
import { getOAuth2AuthorizationUrl } from '@/features/user/util/oauth2';
import { OAuth2Provider } from '@/types/profile';

const providers: {
  id: OAuth2Provider;
  icon: React.ReactNode;
}[] = [
  {
    id: 'GOOGLE',
    icon: <GoogleLogoIcon />,
  },
];

export default function OAuthProviders() {
  const router = useRouter();

  return (
    <div className="flex gap-4 w-full justify-center items-center">
      {providers.map((provider) => (
        <Button
          key={provider.id}
          variant="outline"
          size="icon"
          onClick={() => {
            router.push(getOAuth2AuthorizationUrl(provider.id));
          }}
        >
          {provider.icon}
        </Button>
      ))}
    </div>
  );
}
