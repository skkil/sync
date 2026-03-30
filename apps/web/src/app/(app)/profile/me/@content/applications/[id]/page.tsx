'use client';

import { ArrowLeftIcon } from '@phosphor-icons/react';
import { useTranslations } from 'next-intl';
import Link from 'next/link';

import { Button } from '@/components/ui/button';

export default function Application() {
  const t = useTranslations('pages.profile.applications.details');

  return (
    <div className="mx-auto flex max-w-5xl flex-col gap-6">
      <div className="flex items-center gap-4">
        <Button variant="ghost" size="sm" asChild>
          <Link
            href="/profile/me/applications"
            className="flex items-center gap-2"
          >
            <ArrowLeftIcon />
            <span>{t('back-to-applications')}</span>
          </Link>
        </Button>
      </div>
    </div>
  );
}
