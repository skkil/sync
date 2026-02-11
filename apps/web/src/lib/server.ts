import ky from 'ky';

import { isServer } from '@/util/server';

import { env } from './env';
import SyncError, { ErrorCode } from './error';

const CSRF_COOKIE_NAME = 'XSRF-TOKEN';

interface ErrorResponse {
  detail: string;
  instance: string;
  status: number;
  title: string;
  code: ErrorCode;
}

async function getCsrfToken(): Promise<string | undefined> {
  if (isServer()) {
    try {
      const { cookies } = await import('next/headers');
      const cookieStore = await cookies();
      return cookieStore.get(CSRF_COOKIE_NAME)?.value;
    } catch {
      return undefined;
    }
  } else {
    const value = document.cookie
      .split('; ')
      .find((row) => row.startsWith(`${CSRF_COOKIE_NAME}=`))
      ?.split('=')[1];
    return value ? decodeURIComponent(value) : undefined;
  }
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
        if (request.method !== 'GET' && request.method !== 'HEAD') {
          const csrfToken = await getCsrfToken();
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
