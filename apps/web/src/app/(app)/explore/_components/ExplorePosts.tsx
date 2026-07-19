'use client';

import { useTranslations } from 'next-intl';

import { useGetPostRecommendationsInfinite } from '@/api/__generated__/post/post';
import { PostRecommendationType } from '@/components/feature/post/types/post';
import PostList from '@/components/feature/post/viewer/PostList';
import { toPostSummary } from '@/components/feature/post/viewer/types';
import { Empty, EmptyDescription, EmptyTitle } from '@/components/ui/empty';

const FEED_PAGE_SIZE = '50';

interface ExplorePostsProps {
  type: PostRecommendationType;
}

export default function ExplorePosts({ type }: ExplorePostsProps) {
  const t = useTranslations('pages.explore');
  const { data, fetchNextPage, hasNextPage, isFetchingNextPage, isPending } =
    useGetPostRecommendationsInfinite(
      {
        type,
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

  const posts =
    data?.pages.flatMap((page) => page.data.posts?.nodes ?? []) ?? [];

  return (
    <PostList
      items={posts.map((post) => toPostSummary(post.content))}
      isPending={isPending}
      hasNextPage={!!hasNextPage}
      isFetchingNextPage={isFetchingNextPage}
      fetchNextPage={fetchNextPage}
      empty={
        <Empty className="min-h-80">
          <EmptyTitle>{t(`empty-states.${type}.title`)}</EmptyTitle>
          <EmptyDescription>
            {t(`empty-states.${type}.description`)}
          </EmptyDescription>
        </Empty>
      }
    />
  );
}
