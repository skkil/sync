'use client';

import { useIntersectionObserver } from '@uidotdev/usehooks';
import { useEffect } from 'react';

import { PostStatus, PostType } from '@/components/feature/post/types/post';
import PostPreview from '@/components/feature/post/viewer/PostPreview';
import { Skeleton } from '@/components/ui/skeleton';
import { Spinner } from '@/components/ui/spinner';

interface PostFeedItem {
  content: {
    summary: {
      id: number;
      slug: string;
      type: string;
      status: string;
      title?: string | null;
      author: React.ComponentProps<typeof PostPreview>['author'];
      project?: React.ComponentProps<typeof PostPreview>['project'];
      liked: boolean;
      likeCount: number;
      commentCount: number;
      bookmarked: boolean;
      isAuthor: boolean;
      createdAt: string;
    };
    content: React.ComponentProps<typeof PostPreview>['content']['json'];
  };
}

interface PostFeedListProps {
  posts: PostFeedItem[];
  isPending: boolean;
  isError: boolean;
  hasNextPage?: boolean;
  isFetchingNextPage: boolean;
  fetchNextPage: () => void;
  messages: {
    empty: string;
    error: string;
    end: string;
  };
}

export default function PostFeedList({
  posts,
  isPending,
  isError,
  hasNextPage,
  isFetchingNextPage,
  fetchNextPage,
  messages,
}: PostFeedListProps) {
  const [ref, entry] = useIntersectionObserver({
    threshold: 0.2,
    root: null,
    rootMargin: '400px',
  });

  useEffect(() => {
    if (entry?.isIntersecting && hasNextPage && !isFetchingNextPage) {
      fetchNextPage();
    }
  }, [entry?.isIntersecting, fetchNextPage, hasNextPage, isFetchingNextPage]);

  return (
    <section className="space-y-3">
      {isPending && <PostFeedListSkeleton />}

      {!isPending && isError && (
        <div className="rounded-md border px-4 py-8 text-center">
          <p className="text-sm text-destructive">{messages.error}</p>
        </div>
      )}

      {!isPending && !isError && posts.length === 0 && (
        <div className="rounded-md border px-4 py-8 text-center">
          <p className="text-sm text-muted-foreground">{messages.empty}</p>
        </div>
      )}

      {!isPending && !isError && posts.length > 0 && (
        <>
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

          {!hasNextPage && (
            <div className="py-4 text-center">
              <p className="text-xs text-muted-foreground">{messages.end}</p>
            </div>
          )}
        </>
      )}
    </section>
  );
}

function PostFeedListSkeleton() {
  return (
    <div className="space-y-4">
      {Array.from({ length: 3 }).map((_, index) => (
        <Skeleton key={index} className="h-32 w-full" />
      ))}
    </div>
  );
}
