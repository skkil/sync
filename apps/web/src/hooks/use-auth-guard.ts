'use client';

import { redirect } from 'next/navigation';

import { isAuthenticated, isOnboarded } from '@/lib/auth';
import { useSession } from '@/lib/auth/client';
import ROUTES from '@/util/routes';

/**
 * 클라이언트 컴포넌트 페이지에서 서버 컴포넌트의
 * requireOnboardedSession과 동일한 정책(비로그인 -> /about,
 * 미온보딩 -> /onboarding)을 적용하기 위한 훅. 온보딩은 인증된 사용자에게
 * 항상 요구되므로 온보딩 플로우 자체를 제외하면 예외 없이 이 훅을 쓴다.
 */
export function useAuthGuard() {
  const { data: session, isPending } = useSession();

  if (!isPending) {
    if (!isAuthenticated(session)) {
      redirect(ROUTES.ABOUT());
    } else if (!isOnboarded(session)) {
      redirect(ROUTES.ONBOARDING());
    }
  }

  return { session, isPending };
}
