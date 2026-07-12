'use client';

import { useIntersectionObserver } from '@uidotdev/usehooks';
import { type ReactNode, useEffect } from 'react';

import { ProfileListItem } from '@/components/feature/profile/ProfileListItem';
import { Skeleton } from '@/components/ui/skeleton';
import { Spinner } from '@/components/ui/spinner';

interface InfiniteProfileListItem {
  handle: string;
  name: string;
  imageUrl?: string | null;
}

interface InfiniteProfileListProps {
  users: InfiniteProfileListItem[];
  isPending: boolean;
  isError?: boolean;
  hasNextPage: boolean;
  isFetchingNextPage: boolean;
  fetchNextPage: () => void;
  empty: ReactNode;
  error?: ReactNode;
  skeletonCount?: number;
}

export function InfiniteProfileList({
  users,
  isPending,
  isError,
  hasNextPage,
  isFetchingNextPage,
  fetchNextPage,
  empty,
  error,
  skeletonCount = 6,
}: InfiniteProfileListProps) {
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
      <div className="space-y-2">
        {Array.from({ length: skeletonCount }).map((_, index) => (
          <Skeleton key={index} className="h-14 w-full" />
        ))}
      </div>
    );
  }

  if (isError) {
    return error;
  }

  if (users.length === 0) {
    return empty;
  }

  return (
    <ul className="flex flex-col gap-1">
      {users.map((user) => (
        <ProfileListItem
          key={user.handle}
          handle={user.handle}
          name={user.name}
          imageUrl={user.imageUrl}
        />
      ))}

      <div ref={ref} className="py-4">
        {isFetchingNextPage && (
          <div className="flex justify-center">
            <Spinner />
          </div>
        )}
      </div>
    </ul>
  );
}
