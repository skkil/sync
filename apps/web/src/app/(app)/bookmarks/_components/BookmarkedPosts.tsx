'use client';

import {
  ArrowSquareOutIcon,
  BookmarkSimpleIcon,
  ChatCircleIcon,
  HeartIcon,
} from '@phosphor-icons/react';
import { useIntersectionObserver } from '@uidotdev/usehooks';
import Link from 'next/link';
import { useEffect } from 'react';

import { useGetBookmarkedPostsInfinite } from '@/api/__generated__/bookmark/bookmark';
import type { GetBookmarkedPostsResponsePostsNodesItem } from '@/api/__generated__/types';
import { buttonVariants } from '@/components/ui/button';
import { BaseViewer } from '@/components/ui/editor';
import {
  Empty,
  EmptyDescription,
  EmptyHeader,
  EmptyMedia,
  EmptyTitle,
} from '@/components/ui/empty';
import { Skeleton } from '@/components/ui/skeleton';
import { Spinner } from '@/components/ui/spinner';
import PostBookmarkButton from '@/features/bookmark/components/PostBookmarkButton';
import { cn } from '@/lib/utils';

const BOOKMARKED_POST_PAGE_SIZE = '30';

export default function BookmarkedPosts() {
  const { data, fetchNextPage, hasNextPage, isFetchingNextPage, isPending } =
    useGetBookmarkedPostsInfinite(
      {
        first: BOOKMARKED_POST_PAGE_SIZE,
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
    data?.pages.flatMap((page) => page.data.posts?.nodes ?? []) ?? [];

  if (isPending) {
    return (
      <div className="space-y-4">
        <PageHeader />
        {Array.from({ length: 3 }).map((_, index) => (
          <BookmarkedPostSkeleton key={index} />
        ))}
      </div>
    );
  }

  return (
    <div className="space-y-4">
      <PageHeader />

      {posts.length === 0 ? (
        <Empty className="min-h-80">
          <EmptyMedia variant="icon">
            <BookmarkSimpleIcon />
          </EmptyMedia>
          <EmptyHeader>
            <EmptyTitle>저장한 포스트가 없습니다</EmptyTitle>
            <EmptyDescription>
              다시 보고 싶은 포스트를 북마크하면 여기에 모입니다.
            </EmptyDescription>
          </EmptyHeader>
        </Empty>
      ) : (
        <div className="space-y-4">
          {posts.map((post) => (
            <BookmarkedPostCard key={post.content.id} post={post} />
          ))}
        </div>
      )}

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

function PageHeader() {
  return (
    <div className="space-y-1">
      <h1 className="text-2xl font-semibold">저장한 포스트</h1>
      <p className="text-sm text-muted-foreground">
        북마크한 포스트를 최근 저장순으로 확인하세요.
      </p>
    </div>
  );
}

function BookmarkedPostCard({
  post,
}: {
  post: GetBookmarkedPostsResponsePostsNodesItem;
}) {
  const {
    id,
    slug,
    content,
    createdAt,
    bookmarkedAt,
    author,
    likeCount,
    commentCount,
    bookmarked,
  } = post.content;

  return (
    <article className="rounded-md border p-4">
      <div className="mb-2 flex flex-wrap items-center gap-2 text-sm text-muted-foreground">
        {author && (
          <span className="font-medium text-foreground">{author.name}</span>
        )}
        <span>•</span>
        <span>{new Date(createdAt).toLocaleDateString()}</span>
        <span>•</span>
        <span>저장 {new Date(bookmarkedAt).toLocaleDateString()}</span>
      </div>

      <div className="mb-4">
        <BaseViewer content={content} />
      </div>

      <div className="flex flex-wrap items-center justify-between gap-3">
        <div className="flex gap-4 text-sm text-muted-foreground">
          <span className="inline-flex items-center gap-1">
            <HeartIcon />
            {likeCount}
          </span>
          <span className="inline-flex items-center gap-1">
            <ChatCircleIcon />
            {commentCount}
          </span>
        </div>

        <div className="flex items-center gap-2">
          <Link
            href={`/posts/${slug}`}
            className={cn(
              buttonVariants({ variant: 'ghost', size: 'sm' }),
              'gap-1.5',
            )}
          >
            <ArrowSquareOutIcon />
            보기
          </Link>

          <PostBookmarkButton
            postId={id}
            slug={slug}
            bookmarked={bookmarked}
            isAuthenticated
          />
        </div>
      </div>
    </article>
  );
}

function BookmarkedPostSkeleton() {
  return <Skeleton className="h-40 w-full" />;
}
