'use client';

import { type ReactNode, createContext, useContext, useRef } from 'react';
import { useStore } from 'zustand';

import { type AppStore, type AppStoreType, createAppStore } from '@/store';

export const AppStoreContext = createContext<AppStoreType | null>(null);

export interface AppStoreProviderProps {
  children: ReactNode;
}

export const StoreProvider = ({ children }: AppStoreProviderProps) => {
  const storeRef = useRef<AppStoreType>(null);
  if (!storeRef.current) {
    storeRef.current = createAppStore();
  }

  return (
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
