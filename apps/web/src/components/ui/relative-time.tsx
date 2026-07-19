'use client';

import { formatDistanceToNow } from 'date-fns';
import { enUS, ko } from 'date-fns/locale';
import { useLocale } from 'next-intl';
import { useEffect, useState } from 'react';

import { formatDateStable } from '@/lib/date';

const DATE_FNS_LOCALES = { ko, en: enUS };

export function RelativeTime({ date }: { date: Date | string }) {
  const locale = useLocale();
  const [label, setLabel] = useState(() => formatDateStable(date));

  useEffect(() => {
    // eslint-disable-next-line react-hooks/set-state-in-effect
    setLabel(
      formatDistanceToNow(typeof date === 'string' ? new Date(date) : date, {
        addSuffix: true,
        locale: DATE_FNS_LOCALES[locale as keyof typeof DATE_FNS_LOCALES] ?? ko,
      }),
    );
  }, [date, locale]);

  return <>{label}</>;
}
