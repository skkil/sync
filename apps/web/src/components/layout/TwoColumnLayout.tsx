import Link from 'next/link';
import { ReactNode } from 'react';

import { Copyright } from '@/components/ui/copyright';
import { Logo } from '@/components/ui/logo';
import { cn } from '@/lib/utils';
import ROUTES from '@/util/routes';

interface TwoColumnLayoutProps {
  main: ReactNode;
  side?: ReactNode;
  hideSideOnMobile?: boolean;
  reverseSideOnMobile?: boolean;
}

export function TwoColumnLayout({
  main,
  side,
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
          )}
        >
          {side}
        </div>
      )}
    </div>
  );
}

interface TwoColumnFullPageLayoutProps {
  brandTitle: string;
  brandDescription: string;
  children?: React.ReactNode;
}

export function TwoColumnFullPageLayout({
  brandTitle,
  brandDescription,
  children,
}: TwoColumnFullPageLayoutProps) {
  return (
    <div className="w-full min-h-screen flex flex-col lg:flex-row">
      <div className="relative hidden lg:flex lg:w-1/2 flex-col justify-between p-10 pb-30 overflow-hidden bg-gradient-to-br from-primary to-primary/70 text-white">
        <div
          className="absolute inset-0 opacity-10"
          style={{
            backgroundImage:
              'radial-gradient(circle, currentColor 1.5px, transparent 1.5px)',
            backgroundSize: '28px 28px',
          }}
        />

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
