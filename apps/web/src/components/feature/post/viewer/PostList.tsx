'use client';

import { useIntersectionObserver } from '@uidotdev/usehooks';
import { useTranslations } from 'next-intl';
import { type ReactNode, useEffect } from 'react';

import { Empty, EmptyDescription, EmptyTitle } from '@/components/ui/empty';
import { Skeleton } from '@/components/ui/skeleton';
import { Spinner } from '@/components/ui/spinner';

import { PostPreviewCard } from './PostCard';
import type { PostSummary } from './types';

function DefaultEmpty() {
  const t = useTranslations('components.post.viewer.empty');

  return (
    <Empty className="min-h-80">
      <EmptyTitle>{t('title')}</EmptyTitle>
      <EmptyDescription>{t('description')}</EmptyDescription>
    </Empty>
  );
}

interface PostListProps {
  items: PostSummary[];
  isPending: boolean;
  isError?: boolean;
  hasNextPage: boolean;
  isFetchingNextPage: boolean;
  fetchNextPage: () => void;
  empty?: ReactNode;
  error?: ReactNode;
  end?: ReactNode;
  skeletonCount?: number;
}

export default function PostList({
  items,
  isPending,
  isError,
  hasNextPage,
  isFetchingNextPage,
  fetchNextPage,
  empty = <DefaultEmpty />,
  error,
  end,
  skeletonCount = 3,
}: PostListProps) {
  const [ref, entry] = useIntersectionObserver({
    threshold: 0.2,
    root: null,
    rootMargin: '400px',
  });

  useEffect(() => {
    if (entry?.isIntersecting && hasNextPage && !isFetchingNextPage) {
      fetchNextPage();
    }
  }, [entry?.isIntersecting, hasNextPage, isFetchingNextPage, fetchNextPage]);

  if (isPending) {
    return (
      <div className="space-y-4">
        {Array.from({ length: skeletonCount }).map((_, index) => (
          <Skeleton key={index} className="h-40 w-full" />
        ))}
      </div>
    );
  }

  if (isError) {
    return error;
  }

  if (items.length === 0) {
    return empty;
  }

  return (
    <div className="space-y-4">
      {items.map((item) => (
        <PostPreviewCard key={item.id} summary={item} />
      ))}

      <div ref={ref} className="py-4">
        {isFetchingNextPage && (
          <div className="flex justify-center">
            <Spinner />
          </div>
        )}
      </div>

      {!hasNextPage && end}
    </div>
  );
}
