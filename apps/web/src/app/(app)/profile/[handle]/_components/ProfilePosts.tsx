'use client';

import { useTranslations } from 'next-intl';

import { useGetUserPostsInfinite } from '@/api/__generated__/post/post';
import { useGetProfileByHandle } from '@/api/__generated__/profile/profile';
import PostList from '@/components/feature/post/viewer/PostList';
import PostListMessage from '@/components/feature/post/viewer/error/PostListMessage';
import { toPostSummary } from '@/components/feature/post/viewer/types';

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
