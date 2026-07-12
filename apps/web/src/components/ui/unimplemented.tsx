import { useTranslations } from 'next-intl';
import type { ReactNode } from 'react';

import { cn } from '@/lib/utils';

interface UnimplementedProps {
  children: ReactNode;
  className?: string;
}

export function Unimplemented({ children, className }: UnimplementedProps) {
  const t = useTranslations('components.ui.unimplemented');

  return (
    <div data-slot="unimplemented" className={cn('relative', className)}>
      <div className="pointer-events-none opacity-40 select-none">
        {children}
      </div>

      <div className="absolute inset-0 flex items-center justify-center">
        <span className="bg-card text-muted-foreground rounded-md border px-3 py-1.5 text-sm font-medium shadow-sm">
          {t('label')}
        </span>
      </div>
    </div>
  );
}
