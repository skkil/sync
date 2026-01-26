import ky from 'ky';

import { env } from './env';

export const server = ky.create({
  prefixUrl: env.NEXT_PUBLIC_BACKEND_URL,
  credentials: 'include',
});
