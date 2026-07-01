'use client';

import { QueryClientProvider } from '@tanstack/react-query';
import { ReactQueryDevtools } from '@tanstack/react-query-devtools';
import { usePathname } from 'next/navigation';

import { getQueryClient } from '@/lib/query';

interface QueryProviderProps {
  children?: React.ReactNode;
}

export default function QueryProvider({ children }: QueryProviderProps) {
  const client = getQueryClient();
  const pathname = usePathname();
  const showDevtools =
    process.env.NODE_ENV === 'development' && pathname !== '/';

  return (
    <QueryClientProvider client={client}>
      {children}
      {showDevtools && <ReactQueryDevtools />}
    </QueryClientProvider>
  );
}
