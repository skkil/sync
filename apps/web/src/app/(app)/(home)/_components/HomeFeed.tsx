'use client';

import { useTranslations } from 'next-intl';
import { useMemo, useState } from 'react';

import { useGetPostRecommendationsInfinite } from '@/api/__generated__/post/post';
import { PostType } from '@/components/feature/post/types/post';
import PostList from '@/components/feature/post/viewer/PostList';
import { toPostSummary } from '@/components/feature/post/viewer/types';
import { Tabs, TabsList, TabsTrigger } from '@/components/ui/tabs';

const FEED_PAGE_SIZE = '50';

const FILTERS = [
  { value: 'all', type: undefined },
  { value: 'shorts', type: PostType.SHORT },
  { value: 'articles', type: PostType.LONG },
  { value: 'questions', type: PostType.QUESTION },
] as const;

type Filter = (typeof FILTERS)[number]['value'];

export default function HomeFeed() {
  const t = useTranslations('pages.home.feed.tabs');
  const [filter, setFilter] = useState<Filter>('all');

  const { data, fetchNextPage, hasNextPage, isFetchingNextPage, isPending } =
    useGetPostRecommendationsInfinite(
      {
        type: 'FOLLOWING',
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

  const posts = useMemo(() => {
    const nodes =
      data?.pages.flatMap((page) => page.data.posts?.nodes ?? []) ?? [];
    const activeFilter = FILTERS.find((item) => item.value === filter);

    return activeFilter?.type
      ? nodes.filter((node) => node.content.type === activeFilter.type)
      : nodes;
  }, [data, filter]);

  return (
    <div className="space-y-6">
      <div className="flex items-center justify-between">
        <Tabs
          value={filter}
          onValueChange={(value) => setFilter(value as Filter)}
        >
          <TabsList>
            <TabsTrigger value="all">{t('all')}</TabsTrigger>
            <TabsTrigger value="shorts">{t('shorts')}</TabsTrigger>
            <TabsTrigger value="articles">{t('articles')}</TabsTrigger>
            <TabsTrigger value="questions">{t('questions')}</TabsTrigger>
          </TabsList>
        </Tabs>
      </div>

      <PostList
        items={posts.map((post) => toPostSummary(post.content))}
        isPending={isPending}
        hasNextPage={!!hasNextPage}
        isFetchingNextPage={isFetchingNextPage}
        fetchNextPage={fetchNextPage}
      />
    </div>
  );
}
