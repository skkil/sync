import { ThemeProvider as NextThemeProvider } from 'next-themes';

import { getSession } from '@/lib/auth/session';

interface ThemeProviderProps {
  children?: React.ReactNode;
}

export default async function ThemeProvider({ children }: ThemeProviderProps) {
  const session = await getSession();

  return (
    <NextThemeProvider
      attribute="class"
      defaultTheme={session?.user.theme || 'system'}
    >
      {children}
    </NextThemeProvider>
  );
}
