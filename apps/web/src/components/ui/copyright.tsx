'use client';

import { useTranslations } from 'next-intl';

import { cn } from '@/lib/utils';

interface CopyrightProps {
  className?: string;
}

export function Copyright({ className }: CopyrightProps) {
  const t = useTranslations('components.ui.copyright');

  return (
    <span className={cn('text-xs text-muted-foreground', className)}>
      {t('text', { year: new Date().getFullYear() })}
    </span>
  );
}
