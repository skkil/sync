import type { Metadata } from 'next';
import { Figtree, Geist, Geist_Mono } from 'next/font/google';
import Script from 'next/script';

import AppProvider from '@/components/providers/AppProvider';
import { Toaster } from '@/components/ui/sonner';
import '@/styles/globals.css';

const figtree = Figtree({ subsets: ['latin'], variable: '--font-sans' });

const geistSans = Geist({
  variable: '--font-geist-sans',
  subsets: ['latin'],
});

const geistMono = Geist_Mono({
  variable: '--font-geist-mono',
  subsets: ['latin'],
});

export const metadata: Metadata = {
  title: 'sync',
  description: '',
};

export default function RootLayout({
  children,
}: Readonly<{
  children: React.ReactNode;
}>) {
  return (
    <html lang="en" suppressHydrationWarning className={figtree.variable}>
      <head>
        {process.env.NODE_ENV !== 'production' && (
          <Script id="dev-perf-measure-guard" strategy="beforeInteractive">
            {`
              (() => {
                if (
                  typeof window === 'undefined' ||
                  typeof window.performance === 'undefined' ||
                  typeof window.performance.measure !== 'function'
                ) {
                  return;
                }

                const originalMeasure = window.performance.measure.bind(window.performance);

                window.performance.measure = (...args) => {
                  try {
                    return originalMeasure(...args);
                  } catch (error) {
                    const message = error instanceof Error ? error.message : String(error);
                    if (message.includes('cannot have a negative time stamp')) {
                      return undefined;
                    }
                    throw error;
                  }
                };
              })();
            `}
          </Script>
        )}
      </head>
      <body
        className={`${geistSans.variable} ${geistMono.variable} antialiased`}
      >
        <AppProvider>{children}</AppProvider>
        <Toaster />
      </body>
    </html>
  );
}
