'use client';

import { useTranslations } from 'next-intl';

import { useGetUserPostsInfinite } from '@/api/__generated__/post/post';
import { useGetProfileByHandle } from '@/api/__generated__/profile/profile';

import PostFeedList from './PostFeedList';

const POSTS_PAGE_SIZE = '10';

interface ProfilePostsProps {
  handle: string;
}

export default function ProfilePosts({ handle }: ProfilePostsProps) {
  const t = useTranslations('pages.profile.posts');

  const {
    data: profile,
    isPending: isProfilePending,
    isError: isProfileError,
  } = useGetProfileByHandle(handle);

  const userId = profile?.data.userId;

  const {
    data,
    fetchNextPage,
    hasNextPage,
    isFetchingNextPage,
    isPending: isPostsPending,
    isError: isPostsError,
  } = useGetUserPostsInfinite(
    userId ?? '',
    {
      first: POSTS_PAGE_SIZE,
      after: '',
    },
    {
      query: {
        enabled: !!userId,
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

  const isPending = isProfilePending || (!!userId && isPostsPending);
  const isError = isProfileError || isPostsError;

  return (
    <PostFeedList
      posts={posts}
      isPending={isPending}
      isError={isError}
      hasNextPage={hasNextPage}
      isFetchingNextPage={isFetchingNextPage}
      fetchNextPage={fetchNextPage}
      messages={{
        empty: t('list.empty'),
        error: t('list.error'),
        end: t('list.end'),
      }}
    />
  );
}
