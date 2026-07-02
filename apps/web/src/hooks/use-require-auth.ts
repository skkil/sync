'use client';

import { ModalType } from '@/constants/modal';
import { useModal } from '@/hooks/store';
import { isAuthenticated } from '@/lib/auth';
import { useSession } from '@/lib/auth/client';
import type { AuthRequiredModalPayload } from '@/store/slices/modal';

export function useRequireAuth() {
  const { data: session, isPending } = useSession();
  const { openModal } = useModal();

  const requireAuth = (payload: AuthRequiredModalPayload) => {
    if (isAuthenticated(session)) {
      return true;
    }

    if (!isPending) {
      openModal(ModalType.AUTH_REQUIRED, payload);
    }

    return false;
  };

  return {
    requireAuth,
    isAuthenticated: isAuthenticated(session),
    isPending,
  };
}
