'use client';

import { useIntersectionObserver } from '@uidotdev/usehooks';
import { useTranslations } from 'next-intl';
import { useParams } from 'next/navigation';
import { useEffect } from 'react';
import { toast } from 'sonner';

import { useGetUserPostsInfinite } from '@/api/__generated__/post/post';
import { useGetProfileByHandle } from '@/api/__generated__/profile/profile';
import { Skeleton } from '@/components/ui/skeleton';
import { Spinner } from '@/components/ui/spinner';

import PostCard from './PostCard';

const POSTS_PAGE_SIZE = '10';

export default function Posts() {
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
  } = useGetUserPostsInfinite(
    userId || '',
    {
      first: POSTS_PAGE_SIZE,
      after: '',
    },
    {
      query: {
        getNextPageParam: (lastPage) => {
          const posts = lastPage.data.posts;
          return posts?.pageInfo.hasNextPage
            ? posts.pageInfo.endCursor
            : undefined;
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
    if (isError) {
      toast.error(t('list.error'));
    }
  }, [isError, t]);

  useEffect(() => {
    if (entry?.isIntersecting && hasNextPage && !isFetchingNextPage) {
      fetchNextPage();
    }
  }, [entry?.isIntersecting, hasNextPage, isFetchingNextPage, fetchNextPage]);

  const posts =
    data?.pages.flatMap((page) => page.data.posts?.nodes ?? []) ?? [];

  if (isProfilePending || isPending) {
    return (
      <div className="space-y-4">
        {Array.from({
          length: 3,
        }).map((_, i) => (
          <PostSkeleton key={i} />
        ))}
      </div>
    );
  }

  if (isError) {
    return (
      <div className="text-center py-8">
        <p className="text-sm text-destructive">{t('list.error')}</p>
      </div>
    );
  }

  if (posts.length === 0) {
    return (
      <div className="text-center py-8">
        <p className="text-sm text-muted-foreground">{t('list.empty')}</p>
      </div>
    );
  }

  return (
    <div>
      <div className="space-y-4">
        {posts.map((post) => (
          <PostCard key={post.content.id} post={post.content} />
        ))}
      </div>

      <div ref={ref} className="py-4">
        {isFetchingNextPage && (
          <div className="flex justify-center">
            <Spinner />
          </div>
        )}
      </div>

      {!hasNextPage && posts.length > 0 && (
        <div className="text-center py-4">
          <p className="text-xs text-muted-foreground">{t('list.end')}</p>
        </div>
      )}
    </div>
  );
}

function PostSkeleton() {
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
