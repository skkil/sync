'use client';

import { HashIcon } from '@phosphor-icons/react';
import { useTranslations } from 'next-intl';

import { useGetPublicPostsByTagInfinite } from '@/api/__generated__/post/post';
import PostList from '@/components/feature/post/viewer/PostList';
import PostListMessage from '@/components/feature/post/viewer/error/PostListMessage';
import { toPostViewSource } from '@/components/feature/post/viewer/types';
import {
  Empty,
  EmptyDescription,
  EmptyHeader,
  EmptyMedia,
  EmptyTitle,
} from '@/components/ui/empty';

const TAG_POST_PAGE_SIZE = '30';

interface TagPostsProps {
  name: string;
}

export default function TagPosts({ name }: TagPostsProps) {
  const t = useTranslations('pages.tags');
  const encodedName = encodeURIComponent(name);
  const {
    data,
    fetchNextPage,
    hasNextPage,
    isFetchingNextPage,
    isPending,
    isError,
  } = useGetPublicPostsByTagInfinite(
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

      <PostList
        items={posts.map((post) => toPostViewSource(post.content))}
        isPending={isPending}
        isError={isError}
        hasNextPage={!!hasNextPage}
        isFetchingNextPage={isFetchingNextPage}
        fetchNextPage={fetchNextPage}
        empty={
          <Empty className="min-h-80">
            <EmptyMedia variant="icon">
              <HashIcon />
            </EmptyMedia>
            <EmptyHeader>
              <EmptyTitle>{t('empty.title', { name })}</EmptyTitle>
              <EmptyDescription>{t('empty.description')}</EmptyDescription>
            </EmptyHeader>
          </Empty>
        }
        error={<PostListMessage message={t('error')} variant="destructive" />}
        end={
          <p className="py-4 text-center text-xs text-muted-foreground">
            {t('end')}
          </p>
        }
      />
    </div>
  );
}
