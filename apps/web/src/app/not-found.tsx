'use client';

import { useTranslations } from 'next-intl';
import Link from 'next/link';

import { LinkButton } from '@/components/ui/button';
import { Logo } from '@/components/ui/logo';
import ROUTES from '@/util/routes';

export default function NotFound() {
  const t = useTranslations('pages.not-found');

  return (
    <div className="flex min-h-screen w-full flex-col">
      <header className="mx-auto w-full max-w-5xl px-6 py-4">
        <Link href={ROUTES.HOME()}>
          <Logo />
        </Link>
      </header>

      <main className="flex flex-1 items-center justify-center px-6 py-12">
        <div className="flex w-full max-w-lg flex-col items-center text-center">
          <span className="font-mono text-xs tracking-widest text-muted-foreground uppercase">
            {t('eyebrow')}
          </span>

          <h1 className="mt-4 text-2xl font-semibold tracking-tight text-balance">
            {t('title')}
          </h1>

          <p className="mt-2 text-sm text-muted-foreground text-balance">
            {t('description')}
          </p>

          <div className="mt-8 flex items-center gap-3">
            <LinkButton href={ROUTES.HOME()}>{t('back')}</LinkButton>
            <LinkButton href={ROUTES.EXPLORE()} variant="outline">
              {t('explore')}
            </LinkButton>
          </div>
        </div>
      </main>
    </div>
  );
}
