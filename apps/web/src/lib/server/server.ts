import ky from 'ky';
import { cookies } from 'next/headers';

import { env } from '../env';

async function getCsrfToken(): Promise<string | undefined> {
  try {
    const cookieStore = await cookies();
    return cookieStore.get('XSRF-TOKEN')?.value;
  } catch {
    return undefined;
  }
}

export const server = ky.extend({
  prefixUrl: env.NEXT_PUBLIC_BACKEND_URL,
  credentials: 'include',
  hooks: {
    beforeRequest: [
      async (request) => {
        if (request.method !== 'GET' && request.method !== 'HEAD') {
          const csrfToken = await getCsrfToken();
          if (csrfToken) {
            request.headers.set('X-XSRF-TOKEN', csrfToken);
          }
        }
      },
    ],
  },
});
