'use client';

import { EyeIcon } from '@phosphor-icons/react';
import { useTranslations } from 'next-intl';
import Link from 'next/link';

import { Button } from '@/components/ui/button';

interface ViewProviderAsPublicButtonProps {
  href: string;
}

export default function ViewProviderAsPublicButton({
  href,
}: ViewProviderAsPublicButtonProps) {
  const t = useTranslations();

  return (
    <Link href={href}>
      <Button variant="outline">
        <EyeIcon />
        {t('components.provider.maintainer.view-as-public')}
      </Button>
    </Link>
  );
}
