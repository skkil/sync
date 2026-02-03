'use client';

import { useTranslations } from 'next-intl';

import { Avatar, AvatarFallback } from '@/components/ui/avatar';
import { Button } from '@/components/ui/button';
import useGetProviderQuery from '@/features/provider/api/get-provider';
import { ProviderType } from '@/types/provider';

interface ProviderOverviewProps {
  id: string;
  type: ProviderType;
}

export default function ProviderOverview({ id, type }: ProviderOverviewProps) {
  const t = useTranslations('pages.provider');

  const { data: provider } = useGetProviderQuery(id);
  if (provider?.type !== type) {
    return null;
  }

  return (
    <section className="relative overflow-hidden rounded-3xl border bg-card shadow-sm">
      <div className="relative h-44 w-full bg-muted/60">
        <div className="absolute right-8 top-6 flex items-center gap-2">
          <Button variant="outline">{t('actions.follow')}</Button>
          <Button>{t('actions.review')}</Button>
        </div>
      </div>

      <div className="flex flex-col gap-5 px-8 pb-8 pt-6">
        <div className="flex flex-col gap-4 md:flex-row md:items-end md:justify-between">
          <div className="flex items-center gap-4">
            <Avatar className="h-20 w-20">
              <AvatarFallback />
            </Avatar>

            <div>
              <h1 className="text-2xl font-semibold">{provider.name}</h1>
            </div>
          </div>
        </div>
        <p className="text-sm text-muted-foreground">{provider.description}</p>
      </div>
    </section>
  );
}
