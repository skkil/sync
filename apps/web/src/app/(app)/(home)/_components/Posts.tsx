'use client';

import { useIntersectionObserver } from '@uidotdev/usehooks';
import { useEffect } from 'react';

import { useGetPostRecommendationsInfinite } from '@/api/__generated__/post/post';
import { PostStatus, PostType } from '@/components/feature/post/types/post';
import PostPreview from '@/components/feature/post/viewer/PostPreview';
import { Skeleton } from '@/components/ui/skeleton';
import { Spinner } from '@/components/ui/spinner';

const FEED_PAGE_SIZE = '50';

export default function Posts() {
  const { data, fetchNextPage, hasNextPage, isFetchingNextPage, isPending } =
    useGetPostRecommendationsInfinite(
      {
        first: FEED_PAGE_SIZE,
        after: '',
      },
      {
        query: {
          getNextPageParam: (lastPage) => {
            const pageInfo = lastPage.data.posts?.pageInfo;
            return pageInfo?.hasNextPage
              ? (pageInfo.endCursor ?? undefined)
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
          <PostPreview
            key={post.content.summary.id}
            id={post.content.summary.id}
            slug={post.content.summary.slug}
            type={post.content.summary.type as PostType}
            status={post.content.summary.status as PostStatus}
            title={post.content.summary.title}
            author={post.content.summary.author}
            project={post.content.summary.project}
            content={{ json: post.content.content, media: [] }}
            liked={post.content.summary.liked}
            likeCount={post.content.summary.likeCount}
            commentCount={post.content.summary.commentCount}
            bookmarked={post.content.summary.bookmarked}
            isAuthor={post.content.summary.isAuthor}
            createdAt={post.content.summary.createdAt}
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
