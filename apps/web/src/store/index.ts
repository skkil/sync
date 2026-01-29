import { createStore } from 'zustand/vanilla';

import { ModalSlice, createModalSlice } from './slices/modal';

export type AppStore = ModalSlice;

export const createAppStore = () => {
  return createStore<AppStore>()((...a) => ({
    ...createModalSlice(...a),
  }));
};

export type AppStoreType = ReturnType<typeof createAppStore>;
