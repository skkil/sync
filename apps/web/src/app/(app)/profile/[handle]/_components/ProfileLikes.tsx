'use client';

import { useTranslations } from 'next-intl';

import { useGetLikedPostsInfinite } from '@/api/__generated__/post/post';

import PostFeedList from './PostFeedList';

const LIKES_PAGE_SIZE = '10';

export default function ProfileLikes() {
  const t = useTranslations('pages.profile.tabs.likes');

  const {
    data,
    fetchNextPage,
    hasNextPage,
    isFetchingNextPage,
    isPending,
    isError,
  } = useGetLikedPostsInfinite(
    {
      first: LIKES_PAGE_SIZE,
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
    <PostFeedList
      posts={posts}
      isPending={isPending}
      isError={isError}
      hasNextPage={hasNextPage}
      isFetchingNextPage={isFetchingNextPage}
      fetchNextPage={fetchNextPage}
      messages={{
        empty: t('empty'),
        error: t('error'),
        end: t('end'),
      }}
    />
  );
}
