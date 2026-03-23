import ky from 'ky';

import { getCookies, getCsrfToken, isServer } from '@/util/server';

import { env } from './env';
import SyncError, { ErrorCode } from './error';

interface ErrorResponse {
  detail: string;
  instance: string;
  status: number;
  title: string;
  code: ErrorCode;
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

const getUrl = (url: string) => {
  if (url.startsWith('/')) {
    return url.slice(1);
  }

  return url;
};

const getBody = <T>(c: Response | Request): Promise<T> => {
  const contentType = c.headers.get('content-type');

  if (contentType && contentType.includes('application/json')) {
    return c.json();
  }

  return c.text() as Promise<T>;
};

export const api = async <T>(url: string, options: RequestInit): Promise<T> => {
  const response = await server(getUrl(url), options);
  const data = await getBody<T>(response);

  return { status: response.status, data, headers: response.headers } as T;
};

export type ErrorType<T> = SyncError;
