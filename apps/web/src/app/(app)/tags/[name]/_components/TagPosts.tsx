'use client';

import { HashIcon } from '@phosphor-icons/react';
import { useIntersectionObserver } from '@uidotdev/usehooks';
import { useTranslations } from 'next-intl';
import { useEffect } from 'react';

import { useGetPostsByTagInfinite } from '@/api/__generated__/post/post';
import { PostStatus, PostType } from '@/components/feature/post/types/post';
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

const TAG_POST_PAGE_SIZE = '30';

interface TagPostsProps {
  name: string;
}

export default function TagPosts({ name }: TagPostsProps) {
  const t = useTranslations('pages.tags');
  const encodedName = encodeURIComponent(name);
  const { data, fetchNextPage, hasNextPage, isFetchingNextPage, isPending } =
    useGetPostsByTagInfinite(
      encodedName,
      {
        first: TAG_POST_PAGE_SIZE,
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

  return (
    <div className="mx-auto max-w-3xl space-y-6">
      <header className="space-y-2">
        <div className="flex items-center gap-2 text-muted-foreground">
          <HashIcon size={20} />
          <span className="text-sm font-medium">{t('label')}</span>
        </div>
        <h1 className="break-words text-3xl font-semibold">{name}</h1>
      </header>

      {isPending ? (
        <TagPostsSkeleton />
      ) : posts.length === 0 ? (
        <Empty className="min-h-80">
          <EmptyMedia variant="icon">
            <HashIcon />
          </EmptyMedia>
          <EmptyHeader>
            <EmptyTitle>{t('empty.title', { name })}</EmptyTitle>
            <EmptyDescription>{t('empty.description')}</EmptyDescription>
          </EmptyHeader>
        </Empty>
      ) : (
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

function TagPostsSkeleton() {
  return (
    <div className="space-y-4">
      {Array.from({ length: 3 }).map((_, index) => (
        <Skeleton key={index} className="h-40 w-full" />
      ))}
    </div>
  );
}
