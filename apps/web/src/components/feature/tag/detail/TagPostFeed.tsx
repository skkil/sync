'use client';

import { useTranslations } from 'next-intl';

import { useGetPostsByTagInfinite } from '@/api/__generated__/post/post';
import PostList from '@/components/feature/post/viewer/PostList';
import PostListMessage from '@/components/feature/post/viewer/error/PostListMessage';
import { toPostSummary } from '@/components/feature/post/viewer/types';

const PAGE_SIZE = '10';

interface TagPostFeedProps {
  tagId: string;
}

export default function TagPostFeed({ tagId }: TagPostFeedProps) {
  const t = useTranslations('components.tag.detail.posts');

  const {
    data,
    fetchNextPage,
    hasNextPage,
    isFetchingNextPage,
    isPending,
    isError,
  } = useGetPostsByTagInfinite(
    tagId,
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

  return (
    <PostList
      items={posts.map((post) => toPostSummary(post.content))}
      isPending={isPending}
      isError={isError}
      hasNextPage={!!hasNextPage}
      isFetchingNextPage={isFetchingNextPage}
      fetchNextPage={fetchNextPage}
      empty={<PostListMessage message={t('list.empty')} />}
      error={
        <PostListMessage message={t('list.error')} variant="destructive" />
      }
      end={
        <div className="py-4 text-center">
          <p className="text-xs text-muted-foreground">{t('list.end')}</p>
        </div>
      }
    />
  );
}
