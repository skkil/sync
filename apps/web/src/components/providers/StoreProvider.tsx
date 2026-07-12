'use client';

import { type ReactNode, createContext, useContext, useRef } from 'react';
import { useStore } from 'zustand';

import { type AppStore, type AppStoreType, createAppStore } from '@/store';

export const AppStoreContext = createContext<AppStoreType | null>(null);

export interface AppStoreProviderProps {
  children: ReactNode;
}

export const StoreProvider = ({ children }: AppStoreProviderProps) => {
  const storeRef = useRef<AppStoreType | null>(null);
  // eslint-disable-next-line react-hooks/refs -- standard lazy-ref-init pattern from the React docs, pre-existing
  if (!storeRef.current) {
    storeRef.current = createAppStore();
  }

  return (
    // eslint-disable-next-line react-hooks/refs -- ref is only assigned once above, before this render reads it
    <AppStoreContext.Provider value={storeRef.current}>
      {children}
    </AppStoreContext.Provider>
  );
};

export function useAppStore<T>(selector: (store: AppStore) => T): T {
  const context = useContext(AppStoreContext);
  if (!context) {
    throw new Error('useAppStore must be used within AppStoreProvider');
  }

  return useStore(context, selector);
}
