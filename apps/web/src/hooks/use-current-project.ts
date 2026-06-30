'use client';

import { useShallow } from 'zustand/react/shallow';

import { useAppStore } from '@/components/providers/StoreProvider';

export function useCurrentProject() {
  return useAppStore(
    useShallow((state) => ({
      currentProject: state.currentProject,
      setCurrentProject: state.setCurrentProject,
      clearCurrentProject: state.clearCurrentProject,
    })),
  );
}
