import { useTranslations } from 'next-intl';

import { cn } from '@/lib/utils';

function Spinner({ className, ...props }: React.ComponentProps<'div'>) {
  const t = useTranslations('components.ui.spinner');

  return (
    <div
      className={cn(
        'w-3 h-3 border-1 border-black border-t-transparent rounded-full animate-spin',
        className,
      )}
      role="status"
      aria-label={t('loading')}
      {...props}
    />
  );
}

export { Spinner };
