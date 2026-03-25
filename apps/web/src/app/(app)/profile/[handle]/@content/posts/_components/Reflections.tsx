'use client';

import { useIntersectionObserver } from '@uidotdev/usehooks';
import { useTranslations } from 'next-intl';
import { useParams } from 'next/navigation';
import { useEffect } from 'react';
import { toast } from 'sonner';

import { useGetProfileByHandle } from '@/api/__generated__/profile/profile';
import { useGetUserReflectionsInfinite } from '@/api/__generated__/reflection/reflection';
import { Skeleton } from '@/components/ui/skeleton';
import { Spinner } from '@/components/ui/spinner';

import ReflectionCard from './ReflectionCard';

const REFLECTIONS_PAGE_SIZE = '10';

export default function Reflections() {
  const t = useTranslations('pages.profile.posts');
  const { handle } = useParams();

  const { data: profile, isPending: isProfilePending } = useGetProfileByHandle(
    handle?.toString() || '',
  );

  const userId = profile?.data?.userId;

  const {
    data,
    fetchNextPage,
    hasNextPage,
    isFetchingNextPage,
    isPending,
    isError,
  } = useGetUserReflectionsInfinite(
    userId || '',
    { cursor: '', size: REFLECTIONS_PAGE_SIZE },
    {
      query: {
        getNextPageParam: (lastPage) => {
          const reflections = lastPage.data.reflections;
          return reflections?.hasNext ? reflections.nextCursor : undefined;
        },
        enabled: !!userId,
      },
    },
  );

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

  const reflections =
    data?.pages.flatMap((page) => page.data.reflections?.content ?? []) ?? [];

  if (isProfilePending || isPending) {
    return (
      <div className="space-y-4">
        {Array(3).map((_, i) => (
          <ReflectionSkeleton key={i} />
        ))}
      </div>
    );
  }

  if (isError) {
    toast.error(t('list.error'));

    return (
      <div className="text-center py-8">
        <p className="text-sm text-destructive">{t('list.error')}</p>
      </div>
    );
  }

  if (reflections.length === 0) {
    return (
      <div className="text-center py-8">
        <p className="text-sm text-muted-foreground">{t('list.empty')}</p>
      </div>
    );
  }

  return (
    <div>
      <div className="space-y-4">
        {reflections.map((reflection) => (
          <ReflectionCard key={reflection.id} reflection={reflection} />
        ))}
      </div>

      <div ref={ref} className="py-4">
        {isFetchingNextPage && (
          <div className="flex justify-center">
            <Spinner />
          </div>
        )}
      </div>

      {!hasNextPage && reflections.length > 0 && (
        <div className="text-center py-4">
          <p className="text-xs text-muted-foreground">{t('list.end')}</p>
        </div>
      )}
    </div>
  );
}

function ReflectionSkeleton() {
  return (
    <div className="border rounded-lg p-6">
      <div className="flex items-start gap-4">
        <Skeleton className="h-10 w-10 rounded-full" />
        <div className="flex-1 space-y-3">
          <div className="space-y-2">
            <Skeleton className="h-4 w-32" />
            <Skeleton className="h-3 w-24" />
          </div>
          <Skeleton className="h-20 w-full" />
        </div>
      </div>
    </div>
  );
}
