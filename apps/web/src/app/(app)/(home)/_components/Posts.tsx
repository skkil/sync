'use client';

import { ArrowSquareOutIcon } from '@phosphor-icons/react';
import { useIntersectionObserver } from '@uidotdev/usehooks';
import Link from 'next/link';
import { useEffect } from 'react';

import { useGetRecentFeedInfinite } from '@/api/__generated__/feed/feed';
import type { GetFeedResponseItemsNodesItem } from '@/api/__generated__/types/GetFeedResponseItemsNodesItem';
import { buttonVariants } from '@/components/ui/button';
import { BaseViewer } from '@/components/ui/editor';
import { Skeleton } from '@/components/ui/skeleton';
import { Spinner } from '@/components/ui/spinner';
import PostBookmarkButton from '@/features/bookmark/components/PostBookmarkButton';
import { useSession } from '@/lib/auth/client';

const FEED_PAGE_SIZE = '50';

export default function Posts() {
  const { data: session } = useSession();
  const isAuthenticated = session === undefined ? undefined : !!session?.user;
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

  return (
    <div>
      <div className="space-y-4">
        {posts.map((post) => (
          <PostCard
            key={post.content.id}
            post={post}
            isAuthenticated={isAuthenticated}
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

function PostCard({
  post,
  isAuthenticated,
}: {
  post: GetFeedResponseItemsNodesItem;
  isAuthenticated?: boolean;
}) {
  const {
    id,
    slug,
    content,
    createdAt,
    author,
    likeCount,
    commentCount,
    bookmarked,
  } = post.content;

  return (
    <div className="rounded-md border p-4">
      <div className="mb-2 flex items-center gap-2 text-sm text-muted-foreground">
        {author && <span>{author.name}</span>}
        <span>•</span>
        <span>{new Date(createdAt).toLocaleDateString()}</span>
      </div>

      <div className="mb-3">
        <BaseViewer content={content} />
      </div>

      <div className="flex items-center justify-between gap-4">
        <div className="flex gap-4 text-sm text-muted-foreground">
          <span>{likeCount} likes</span>
          <span>{commentCount} comments</span>
        </div>

        <div className="flex items-center gap-2">
          <Link
            href={`/posts/${slug}`}
            className={buttonVariants({
              variant: 'ghost',
              size: 'sm',
              className: 'gap-1.5',
            })}
          >
            <ArrowSquareOutIcon />
            보기
          </Link>

          <PostBookmarkButton
            postId={id}
            slug={slug}
            bookmarked={bookmarked}
            isAuthenticated={isAuthenticated}
          />
        </div>
      </div>
    </div>
  );
}

function PostSkeleton() {
  return <Skeleton className="h-32 w-full" />;
}
