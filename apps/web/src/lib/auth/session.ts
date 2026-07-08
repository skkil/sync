import { headers } from 'next/headers';
import { cache } from 'react';

import { auth } from './index';

/**
 * 요청 1회당 한 번만 세션을 조회하도록 memoize한다.
 * spring-session 훅은 캐시 미스 시 백엔드 호출 + DB 쓰기 + 세션 로테이션을
 * 수행하므로, 이 함수를 거치지 않고 auth.api.getSession을 직접 호출하지 않는다.
 */
export const getSession = cache(async () => {
  return auth.api.getSession({ headers: await headers() });
});
