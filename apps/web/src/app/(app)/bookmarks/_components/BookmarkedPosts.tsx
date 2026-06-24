'use client';

import { BookmarkSimpleIcon } from '@phosphor-icons/react';
import { useIntersectionObserver } from '@uidotdev/usehooks';
import { useEffect } from 'react';

import { useGetBookmarkedPostsInfinite } from '@/api/__generated__/bookmark/bookmark';
import PostPreview from '@/components/feature/post/viewer/PostPreview';
import {
  Empty,
  EmptyDescription,
  EmptyHeader,
  EmptyMedia,
  EmptyTitle,
} from '@/components/ui/empty';
import { Skeleton } from '@/components/ui/skeleton';
import { Spinner } from '@/components/ui/spinner';

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
            <PostPreview
              key={post.content.id}
              id={post.content.id}
              author={post.content.author ?? { id: 0, name: '' }}
              content={{ json: post.content.content, media: [] }}
              likeCount={post.content.likeCount}
              commentCount={post.content.commentCount}
              bookmarked={post.content.bookmarked}
            />
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

function BookmarkedPostSkeleton() {
  return <Skeleton className="h-40 w-full" />;
}
