'use client';

import { useShallow } from 'zustand/react/shallow';

import { useAppStore } from '@/components/providers/StoreProvider';

export function useModal() {
  return useAppStore(
    useShallow((state) => ({
      isOpen: state.isOpen,
      type: state.type,
      openModal: state.openModal,
      closeModal: state.closeModal,
    })),
  );
}
