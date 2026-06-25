'use client';

import { formatDistanceToNow } from 'date-fns';
import { useEffect, useState } from 'react';

import { formatDateStable } from '@/lib/date';

export function RelativeTime({ date }: { date: Date | string }) {
  const [label, setLabel] = useState(() => formatDateStable(date));

  useEffect(() => {
    // eslint-disable-next-line react-hooks/set-state-in-effect
    setLabel(
      formatDistanceToNow(typeof date === 'string' ? new Date(date) : date, {
        addSuffix: true,
      }),
    );
  }, [date]);

  return <>{label}</>;
}
