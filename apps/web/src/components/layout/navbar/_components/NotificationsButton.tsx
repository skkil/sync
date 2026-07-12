'use client';

import { BellIcon } from '@phosphor-icons/react';
import { useTranslations } from 'next-intl';

import { Button } from '@/components/ui/button';
import {
  DropdownMenu,
  DropdownMenuContent,
  DropdownMenuTrigger,
} from '@/components/ui/dropdown-menu';

export default function NotificationsButton() {
  const t = useTranslations('components.navigation');

  return (
    <DropdownMenu>
      <DropdownMenuTrigger asChild>
        <Button
          variant="ghost"
          size="icon"
          aria-label={t('notifications.aria-label')}
        >
          <BellIcon size={20} />
        </Button>
      </DropdownMenuTrigger>
      <DropdownMenuContent align="end" className="w-80">
        {t('notifications.placeholder')}
      </DropdownMenuContent>
    </DropdownMenu>
  );
}
