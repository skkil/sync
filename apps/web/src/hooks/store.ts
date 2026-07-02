'use client';

import { useShallow } from 'zustand/react/shallow';

import { useAppStore } from '@/components/providers/StoreProvider';

export function useModal() {
  return useAppStore(
    useShallow((state) => ({
      isOpen: state.isOpen,
      type: state.type,
      payload: state.payload,
      openModal: state.openModal,
      closeModal: state.closeModal,
    })),
  );
}
