'use client';

import { HashIcon, MagnifyingGlassIcon } from '@phosphor-icons/react';
import { useTranslations } from 'next-intl';
import Link from 'next/link';

import { useSearchTags } from '@/api/__generated__/tag/tag';
import { Badge } from '@/components/ui/badge';
import {
  Empty,
  EmptyHeader,
  EmptyMedia,
  EmptyTitle,
} from '@/components/ui/empty';
import { Skeleton } from '@/components/ui/skeleton';
import ROUTES from '@/util/routes';

interface SearchTagResultsProps {
  query: string;
}

export default function SearchTagResults({ query }: SearchTagResultsProps) {
  const t = useTranslations('pages.search');
  const { data, isPending } = useSearchTags(
    { query },
    { query: { enabled: !!query, staleTime: 0 } },
  );

  if (isPending) {
    return (
      <div className="space-y-3">
        {Array.from({ length: 4 }).map((_, index) => (
          <Skeleton key={index} className="h-16 w-full" />
        ))}
      </div>
    );
  }

  const tags = data?.data.tags ?? [];

  if (tags.length === 0) {
    return (
      <Empty className="min-h-60">
        <EmptyMedia variant="icon">
          <MagnifyingGlassIcon />
        </EmptyMedia>
        <EmptyHeader>
          <EmptyTitle>{t('results.no-results', { query })}</EmptyTitle>
        </EmptyHeader>
      </Empty>
    );
  }

  return (
    <div className="divide-y">
      {tags.map((tag) => (
        <Link
          key={tag.id}
          href={ROUTES.TAG(String(tag.id))}
          className="flex items-center justify-between gap-4 py-4"
        >
          <div className="flex min-w-0 items-center gap-3">
            <span className="flex size-9 shrink-0 items-center justify-center rounded-md bg-muted text-muted-foreground">
              <HashIcon size={18} />
            </span>
            <div className="min-w-0">
              <h3 className="truncate font-semibold">{tag.name}</h3>
              {tag.description && (
                <p className="mt-1 line-clamp-1 text-sm text-muted-foreground">
                  {tag.description}
                </p>
              )}
            </div>
          </div>

          <Badge variant="secondary">
            {t('tags.post-count', { count: tag.postCount })}
          </Badge>
        </Link>
      ))}
    </div>
  );
}
