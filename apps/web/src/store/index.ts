import { createJSONStorage, persist } from 'zustand/middleware';
import { createStore } from 'zustand/vanilla';

import { ModalSlice, createModalSlice } from './slices/modal';
import { ProjectSlice, createProjectSlice } from './slices/project';

export type AppStore = ModalSlice & ProjectSlice;

export const createAppStore = () => {
  return createStore<AppStore>()(
    persist(
      (...a) => ({
        ...createModalSlice(...a),
        ...createProjectSlice(...a),
      }),
      {
        name: 'app-store',
        storage: createJSONStorage(() => sessionStorage),
        partialize: (state) => ({ currentProject: state.currentProject }),
      },
    ),
  );
};

export type AppStoreType = ReturnType<typeof createAppStore>;
