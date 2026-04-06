import { ThemeProvider as NextThemeProvider } from 'next-themes';
import { headers } from 'next/headers';

import { auth } from '@/lib/auth';

interface ThemeProviderProps {
  children?: React.ReactNode;
}

export default async function ThemeProvider({ children }: ThemeProviderProps) {
  const session = await auth.api.getSession({
    headers: await headers(),
  });

  return (
    <NextThemeProvider
      attribute="class"
      defaultTheme={session?.user.theme || 'system'}
    >
      {children}
    </NextThemeProvider>
  );
}
