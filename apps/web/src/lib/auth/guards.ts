import { redirect } from 'next/navigation';

import ROUTES from '@/util/routes';

import { isAuthenticated, isOnboarded } from './index';
import { getSession } from './session';

/**
 * 비로그인 사용자는 항상 /about 으로 보낸다는 정책의 단일 기준점.
 * 페이지별로 리다이렉트 대상을 따로 정하지 않도록 여기서만 관리한다.
 *
 * 온보딩이 완료되지 않은 사용자도 통과시켜야 하는 온보딩 플로우 자체를
 * 제외하면, 일반 페이지는 항상 requireOnboardedSession을 사용한다.
 */
export async function requireSession() {
  const session = await getSession();

  if (!isAuthenticated(session)) {
    redirect(ROUTES.ABOUT());
  }

  return session;
}

export async function requireOnboardedSession() {
  const session = await requireSession();

  if (!isOnboarded(session)) {
    redirect(ROUTES.ONBOARDING());
  }

  return session;
}
