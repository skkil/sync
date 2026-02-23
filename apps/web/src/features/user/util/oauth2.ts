import { env } from '@/lib/env';
import { OAuth2Provider } from '@/types/profile';

function getClientId(provider: OAuth2Provider) {
  switch (provider) {
    case 'GOOGLE':
      return 'google';
  }
}

export function getOAuth2AuthorizationUrl(provider: OAuth2Provider) {
  return `${env.NEXT_PUBLIC_BACKEND_URL}/oauth2/authorization/${getClientId(provider)}`;
}
