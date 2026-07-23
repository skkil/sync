import type { Metadata } from 'next';
import { getTranslations } from 'next-intl/server';
import { JetBrains_Mono } from 'next/font/google';
import localFont from 'next/font/local';

import AppProvider from '@/components/providers/AppProvider';
import { Toaster } from '@/components/ui/sonner';
import { INDEXABLE_ROBOTS, getSiteUrl } from '@/lib/seo';
import '@/styles/globals.css';

const pretendard = localFont({
  src: '../../public/fonts/PretendardVariable.woff2',
  variable: '--font-sans',
  display: 'swap',
  weight: '45 920',
});

const jetbrainsMono = JetBrains_Mono({
  variable: '--font-mono',
  subsets: ['latin'],
});

export async function generateMetadata(): Promise<Metadata> {
  const t = await getTranslations('metadata');

  return {
    metadataBase: getSiteUrl(),
    title: {
      default: 'sync',
      template: '%s | sync',
    },
    description: t('description'),
    applicationName: 'sync',
    creator: 'sync',
    publisher: 'sync',
    robots: INDEXABLE_ROBOTS,
  };
}

export default function RootLayout({
  children,
}: Readonly<{
  children: React.ReactNode;
}>) {
  return (
    <html lang="ko" suppressHydrationWarning className={pretendard.variable}>
      <body className={`${jetbrainsMono.variable} antialiased`}>
        <AppProvider>{children}</AppProvider>
        <Toaster />
      </body>
    </html>
  );
}
