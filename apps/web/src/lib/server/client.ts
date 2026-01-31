import ky from 'ky';

import { env } from '../env';

export function getCookie(name: string): string | undefined {
  if (typeof document === 'undefined') {
    return undefined;
  }

  const value = document.cookie
    .split('; ')
    .find((row) => row.startsWith(`${name}=`))
    ?.split('=')[1];
  return value ? decodeURIComponent(value) : undefined;
}

export const server = ky.extend({
  prefixUrl: env.NEXT_PUBLIC_BACKEND_URL,
  credentials: 'include',
  hooks: {
    beforeRequest: [
      (request) => {
        if (request.method !== 'GET' && request.method !== 'HEAD') {
          const csrfToken = getCookie('XSRF-TOKEN');
          if (csrfToken) {
            request.headers.set('X-XSRF-TOKEN', csrfToken);
          }
        }
      },
    ],
  },
});
