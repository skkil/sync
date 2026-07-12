import { useTranslations } from 'next-intl';
import Link from 'next/link';
import type { ReactNode } from 'react';

import { APP_NAME } from '@/constants/app';
import ROUTES from '@/util/routes';

export default function AuthTermsNotice() {
  const t = useTranslations('pages.auth');

  return (
    <p className="text-center text-xs text-muted-foreground">
      {t.rich('terms', {
        appName: APP_NAME,
        terms: (chunks: ReactNode) => (
          <Link href={ROUTES.TERMS()} className="underline">
            {chunks}
          </Link>
        ),
        privacy: (chunks: ReactNode) => (
          <Link href={ROUTES.PRIVACY()} className="underline">
            {chunks}
          </Link>
        ),
      })}
    </p>
  );
}
