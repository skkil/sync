'use client';

import { useIntersectionObserver } from '@uidotdev/usehooks';
import { useTranslations } from 'next-intl';
import { useEffect } from 'react';

import { useGetPostsByProjectInfinite } from '@/api/__generated__/post/post';
import type { GetPostsResponsePostsNodesItemContent } from '@/api/__generated__/types';
import { BaseViewer } from '@/components/ui/editor';
import { Skeleton } from '@/components/ui/skeleton';
import { Spinner } from '@/components/ui/spinner';

const PAGE_SIZE = '10';

interface ProjectPostsProps {
  handle: string;
}

export default function ProjectPosts({ handle }: ProjectPostsProps) {
  const t = useTranslations('pages.projects.project.posts');

  const {
    data,
    fetchNextPage,
    hasNextPage,
    isFetchingNextPage,
    isPending,
    isError,
  } = useGetPostsByProjectInfinite(
    handle,
    { first: PAGE_SIZE },
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

  const posts =
    data?.pages.flatMap((page) => page.data.posts?.nodes ?? []) ?? [];

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
      {isPending && <ProjectPostsSkeleton />}

      {!isPending && isError && (
        <div className="rounded-md border px-4 py-8 text-center">
          <p className="text-sm text-destructive">{t('list.error')}</p>
        </div>
      )}

      {!isPending && !isError && posts.length === 0 && (
        <div className="rounded-md border px-4 py-8 text-center">
          <p className="text-sm text-muted-foreground">{t('list.empty')}</p>
        </div>
      )}

      {!isPending && !isError && posts.length > 0 && (
        <>
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

          {!hasNextPage && (
            <div className="py-4 text-center">
              <p className="text-xs text-muted-foreground">{t('list.end')}</p>
            </div>
          )}
        </>
      )}
    </section>
  );
}

function PostCard({ post }: { post: GetPostsResponsePostsNodesItemContent }) {
  return (
    <article className="rounded-md border p-4">
      <div className="mb-2 flex flex-wrap items-center gap-2 text-sm text-muted-foreground">
        {post.author && <span>{post.author.name}</span>}
        <span>{new Date(post.createdAt).toLocaleDateString()}</span>
      </div>

      <BaseViewer content={post.content} />
    </article>
  );
}

function ProjectPostsSkeleton() {
  return (
    <div className="space-y-4">
      {Array.from({ length: 3 }).map((_, index) => (
        <Skeleton key={index} className="h-32 w-full" />
      ))}
    </div>
  );
}
