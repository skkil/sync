'use client';

import { SealCheckIcon, WarningIcon } from '@phosphor-icons/react';
import { useTranslations } from 'next-intl';
import Link from 'next/link';

import { useGetMyProvidersInfinite } from '@/api/__generated__/providers/providers';
import type { GetProvidersResponseProvidersContentItem } from '@/api/__generated__/types';
import { Avatar, AvatarFallback } from '@/components/ui/avatar';
import { Button } from '@/components/ui/button';
import {
  Tooltip,
  TooltipContent,
  TooltipTrigger,
} from '@/components/ui/tooltip';

const PAGE_SIZE = 25;

export default function MyProviders() {
  const t = useTranslations('pages.my-providers');

  const {
    data,
    fetchNextPage,
    hasNextPage,
    isFetchingNextPage,
    isPending,
    error,
  } = useGetMyProvidersInfinite(
    { size: PAGE_SIZE.toString() },
    {
      query: {
        getNextPageParam: (lastPage) => {
          const providers = lastPage.data.providers;
          return providers?.hasNext ? providers.nextCursor : undefined;
        },
      },
    },
  );

  const providers =
    data?.pages.flatMap((page) => page.data.providers?.content ?? []) ?? [];

  if (error) {
    return (
      <div className="mx-auto max-w-5xl p-6">
        <div className="rounded-lg border border-destructive bg-destructive/10 p-6 text-center">
          <p className="text-destructive">{t('error')}</p>
        </div>
      </div>
    );
  }

  return (
    <div className="mx-auto max-w-5xl p-6">
      <div className="mb-6">
        <h1 className="mb-2 text-xl font-bold">{t('title')}</h1>
        <p className="text-sm text-muted-foreground">{t('description')}</p>
      </div>

      <div className="min-h-96">
        {isPending ? (
          <div className="space-y-3">
            {Array.from({ length: 5 }).map((_, i) => (
              <div key={i} className="h-20 animate-pulse rounded-lg bg-muted" />
            ))}
          </div>
        ) : providers.length > 0 ? (
          <div className="space-y-3">
            {providers.map((provider) => (
              <ProviderCard key={provider.id} provider={provider} />
            ))}
          </div>
        ) : (
          <div className="rounded-lg border border-dashed p-12 text-center">
            <p className="text-muted-foreground">{t('empty')}</p>
          </div>
        )}

        {hasNextPage && (
          <div className="mt-6 flex justify-center">
            <Button
              onClick={() => fetchNextPage()}
              disabled={isFetchingNextPage}
              variant="outline"
              size="lg"
            >
              {isFetchingNextPage ? t('loading') : t('load-more')}
            </Button>
          </div>
        )}
      </div>
    </div>
  );
}

function ProviderCard({
  provider,
}: {
  provider: GetProvidersResponseProvidersContentItem;
}) {
  const t = useTranslations('pages.my-providers');

  return (
    <Link
      href={`/${provider.type.toLowerCase()}/${provider.id}`}
      className="block"
    >
      <div className="rounded-lg border bg-card p-4 transition-colors hover:bg-muted/50">
        <div className="flex items-start gap-4">
          <Avatar className="h-12 w-12">
            <AvatarFallback />
          </Avatar>
          <div className="min-w-0 flex-1">
            <div className="mb-2 flex items-start justify-between gap-2">
              <div className="flex items-center gap-2">
                <h3 className="font-semibold text-foreground">
                  {provider.name}
                </h3>

                <span>
                  {!provider.isVerified ? (
                    <Tooltip>
                      <TooltipTrigger asChild>
                        <WarningIcon />
                      </TooltipTrigger>
                      <TooltipContent>
                        {t('provider.unverified.tooltip')}
                      </TooltipContent>
                    </Tooltip>
                  ) : (
                    <SealCheckIcon />
                  )}
                </span>
              </div>
              <div className="flex flex-shrink-0 gap-2"></div>
            </div>
          </div>
        </div>
      </div>
    </Link>
  );
}
