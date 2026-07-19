'use client';

import { QueryClientProvider } from '@tanstack/react-query';
import { ReactQueryDevtools } from '@tanstack/react-query-devtools';
import { useEffect } from 'react';

import { getQueryClient } from '@/lib/query';
import { primeCsrfToken } from '@/lib/server';

interface QueryProviderProps {
  children?: React.ReactNode;
}

export default function QueryProvider({ children }: QueryProviderProps) {
  const client = getQueryClient();

  useEffect(() => {
    void primeCsrfToken();
  }, []);

  return (
    <QueryClientProvider client={client}>
      {children}
      <ReactQueryDevtools />
    </QueryClientProvider>
  );
}
