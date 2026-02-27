import ky from 'ky';

import { getCsrfToken, getCsrfTokenServer } from '@/util/cookie';
import { isServer } from '@/util/server';

import { env } from './env';
import SyncError, { ErrorCode } from './error';

interface ErrorResponse {
  detail: string;
  instance: string;
  status: number;
  title: string;
  code: ErrorCode;
}

async function getCookies() {
  if (isServer()) {
    try {
      const { cookies } = await import('next/headers');
      const cookieStore = await cookies();
      const allCookies = cookieStore.getAll();

      return allCookies
        .map((cookie) => `${cookie.name}=${cookie.value}`)
        .join('; ');
    } catch {
      return undefined;
    }
  }

  return undefined;
}

export const server = ky.extend({
  prefixUrl: env.NEXT_PUBLIC_BACKEND_URL,
  credentials: 'include',
  retry: {
    limit: 1,
    methods: ['post', 'put', 'patch', 'delete'],
    statusCodes: [403],
  },
  hooks: {
    beforeRequest: [
      async (request) => {
        if (isServer()) {
          const cookies = await getCookies();
          if (cookies) {
            request.headers.set('Cookie', cookies);
          }
        }
      },
      async (request) => {
        if (request.method !== 'GET' && request.method !== 'HEAD') {
          const csrfToken = await (isServer()
            ? getCsrfTokenServer()
            : getCsrfToken());
          if (csrfToken) {
            request.headers.set('X-XSRF-TOKEN', csrfToken);
          }
        }
      },
    ],
    beforeError: [
      async (error) => {
        const { response } = error;

        if (response.status === 401 || response.status === 403) {
          return error;
        }

        const body = await response.json<ErrorResponse>();
        throw new SyncError(body.detail, body.code);
      },
    ],
  },
});
