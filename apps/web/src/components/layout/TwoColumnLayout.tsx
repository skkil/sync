import Image from 'next/image';
import Link from 'next/link';
import { ReactNode } from 'react';

import { Copyright } from '@/components/ui/copyright';
import { Logo } from '@/components/ui/logo';
import { cn } from '@/lib/utils';
import ROUTES from '@/util/routes';

interface TwoColumnLayoutProps {
  main: ReactNode;
  side?: ReactNode;
  sideFooter?: ReactNode;
  sideViewportScrollable?: boolean;
  hideSideOnMobile?: boolean;
  reverseSideOnMobile?: boolean;
}

export function TwoColumnLayout({
  main,
  side,
  sideFooter,
  sideViewportScrollable,
  hideSideOnMobile,
  reverseSideOnMobile,
}: TwoColumnLayoutProps) {
  return (
    <div
      className={cn(
        'grid grid-cols-1 items-start gap-6',
        side && 'lg:grid-cols-3',
      )}
    >
      <div
        className={cn(
          side && 'lg:col-span-2',
          reverseSideOnMobile && 'order-2 lg:order-1',
        )}
      >
        {main}
      </div>

      {side && (
        <div
          className={cn(
            'lg:sticky lg:top-7',
            hideSideOnMobile && 'hidden lg:block',
            reverseSideOnMobile && 'order-1 lg:order-2',
            sideViewportScrollable &&
              'lg:flex lg:h-[calc(100svh-7.5rem)] lg:min-h-0 lg:flex-col lg:overflow-clip',
          )}
        >
          <div
            className={cn(
              sideViewportScrollable &&
                'lg:min-h-0 lg:flex-1 lg:overflow-y-auto lg:overscroll-contain lg:pr-2 lg:pl-1 lg:[scrollbar-gutter:stable]',
            )}
          >
            {side}
          </div>
          {sideFooter && (
            <div className={cn(sideViewportScrollable && 'lg:shrink-0')}>
              {sideFooter}
            </div>
          )}
        </div>
      )}
    </div>
  );
}

export const BRAND_ART = {
  login: '/assets/auth/login.webp',
  register: '/assets/auth/register.webp',
  onboarding: '/assets/auth/onboarding.webp',
} as const;

const BRAND_ART_ALT = '';
const BRAND_ART_SIZES = '50vw';

interface TwoColumnFullPageLayoutProps {
  brandTitle: string;
  brandDescription: string;
  artSrc?: string;
  children?: React.ReactNode;
}

export function TwoColumnFullPageLayout({
  brandTitle,
  brandDescription,
  artSrc = BRAND_ART.login,
  children,
}: TwoColumnFullPageLayoutProps) {
  return (
    <div className="light bg-background text-foreground w-full min-h-screen flex flex-col lg:flex-row">
      <div className="relative hidden lg:flex lg:w-1/2 flex-col justify-between p-10 pb-30 overflow-hidden bg-[#073b31] text-white">
        <Image
          src={artSrc}
          alt={BRAND_ART_ALT}
          fill
          priority
          sizes={BRAND_ART_SIZES}
          className="object-cover"
        />

        <div className="absolute inset-0 bg-gradient-to-t from-[#04231d]/70 via-[#04231d]/20 to-transparent" />

        <Link href={ROUTES.ABOUT()} className="relative z-10 [&_svg]:invert">
          <Logo />
        </Link>

        <div className="relative z-10 max-w-md">
          <h1 className="text-3xl font-light mb-3">{brandTitle}</h1>
          <p className="text-white/80">{brandDescription}</p>
        </div>
      </div>

      <div className="flex-1 flex flex-col">
        <header className="p-6 lg:hidden">
          <Link href={ROUTES.ABOUT()}>
            <Logo />
          </Link>
        </header>

        <main className="flex-1 flex items-center justify-center p-6">
          <div className="w-full max-w-md min-h-100 flex flex-col justify-start">
            {children}
          </div>
        </main>

        <footer className="p-6 text-center">
          <Copyright />
        </footer>
      </div>
    </div>
  );
}
