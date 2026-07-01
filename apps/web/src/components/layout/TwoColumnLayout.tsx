import { ReactNode } from 'react';

import { cn } from '@/lib/utils';

interface TwoColumnLayoutProps {
  main: ReactNode;
  side?: ReactNode;
  hideSideOnMobile?: boolean;
}

export default function TwoColumnLayout({
  main,
  side,
  hideSideOnMobile,
}: TwoColumnLayoutProps) {
  return (
    <div
      className={cn(
        'grid grid-cols-1 items-start gap-6',
        side && 'lg:grid-cols-3',
      )}
    >
      <div className={cn(side && 'lg:col-span-2')}>{main}</div>

      {side && (
        <div
          className={cn(
            'lg:sticky lg:top-7',
            hideSideOnMobile && 'hidden lg:block',
          )}
        >
          {side}
        </div>
      )}
    </div>
  );
}
