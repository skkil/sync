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
  isFetchNextPageError?: boolean;
  fetchNextPage: () => void;
  empty?: ReactNode;
  error?: ReactNode;
  nextPageError?: ReactNode;
  end?: ReactNode;
  skeletonCount?: number;
}

export default function PostList({
  items,
  isPending,
  isError,
  hasNextPage,
  isFetchingNextPage,
  isFetchNextPageError = false,
  fetchNextPage,
  empty = <DefaultEmpty />,
  error,
  nextPageError,
  end,
  skeletonCount = 3,
}: PostListProps) {
  const [ref, entry] = useIntersectionObserver({
    threshold: 0.2,
    root: null,
    rootMargin: '400px',
  });

  useEffect(() => {
    if (
      entry?.isIntersecting &&
      hasNextPage &&
      !isFetchingNextPage &&
      !isFetchNextPageError
    ) {
      fetchNextPage();
    }
  }, [
    entry?.isIntersecting,
    hasNextPage,
    isFetchingNextPage,
    isFetchNextPageError,
    fetchNextPage,
  ]);

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
    <div>
      {/* 카드를 띄우는 대신 가는 선으로만 글을 나눈다. 구분선은 카드가 아니라
          바깥 래퍼에 긋는다 — Tailwind 의 `divide-y` 는 `:where()` 로 감싼
          0순위 규칙이라, 카드에 걸린 `border-0` 에 그대로 덮인다. */}
      <div className="divide-hairline-strong divide-y">
        {items.map((item) => (
          <div key={item.id}>
            <PostPreviewCard summary={item} surface="flat" />
          </div>
        ))}
      </div>

      <div ref={ref} className="py-4">
        {isFetchingNextPage && (
          <div className="flex justify-center">
            <Spinner />
          </div>
        )}
        {isFetchNextPageError && nextPageError}
      </div>

      {!hasNextPage && end}
    </div>
  );
}
