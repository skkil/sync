'use client';

import { useIntersectionObserver } from '@uidotdev/usehooks';
import { useEffect } from 'react';

import { useGetRecentFeedInfinite } from '@/api/__generated__/feed/feed';
import { Skeleton } from '@/components/ui/skeleton';
import { Spinner } from '@/components/ui/spinner';

import PostPreview from './PostPreview';

const FEED_PAGE_SIZE = '50';

interface PublicPostFeedProps {
  emptyMessage?: string;
}

export default function PublicPostFeed({ emptyMessage }: PublicPostFeedProps) {
  const { data, fetchNextPage, hasNextPage, isFetchingNextPage, isPending } =
    useGetRecentFeedInfinite(
      {
        first: FEED_PAGE_SIZE,
        after: '',
      },
      {
        query: {
          getNextPageParam: (lastPage) => {
            const items = lastPage.data.items;
            return items?.pageInfo.hasNextPage
              ? items.pageInfo.endCursor
              : undefined;
          },
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

  const posts =
    data?.pages.flatMap((page) => page.data.items?.nodes ?? []) ?? [];

  if (isPending) {
    return (
      <div className="space-y-4">
        {Array.from({ length: 3 }).map((_, i) => (
          <PostSkeleton key={i} />
        ))}
      </div>
    );
  }

  if (posts.length === 0 && emptyMessage) {
    return (
      <div className="rounded-md border px-4 py-12 text-center">
        <p className="text-muted-foreground text-sm">{emptyMessage}</p>
      </div>
    );
  }

  return (
    <div>
      <div className="space-y-4">
        {posts.map((post) => (
          <PostPreview
            key={post.content.id}
            id={post.content.id}
            slug={post.content.slug}
            author={post.content.author}
            content={{ json: post.content.content, media: [] }}
            likeCount={post.content.likeCount}
            commentCount={post.content.commentCount}
            bookmarked={post.content.bookmarked}
            createdAt={post.content.createdAt}
            project={post.content.project?.handle}
          />
        ))}
      </div>

      <div ref={ref} className="py-4">
        {isFetchingNextPage && (
          <div className="flex justify-center">
            <Spinner />
          </div>
        )}
      </div>
    </div>
  );
}

function PostSkeleton() {
  return <Skeleton className="h-32 w-full" />;
}
