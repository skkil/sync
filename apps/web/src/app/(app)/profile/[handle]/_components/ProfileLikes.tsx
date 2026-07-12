'use client';

import { useTranslations } from 'next-intl';

import { useGetLikedPostsInfinite } from '@/api/__generated__/post/post';
import PostList from '@/components/feature/post/viewer/PostList';
import PostListMessage from '@/components/feature/post/viewer/error/PostListMessage';
import { toPostViewSource } from '@/components/feature/post/viewer/types';

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
    <PostList
      items={posts.map((post) => toPostViewSource(post.content))}
      isPending={isPending}
      isError={isError}
      hasNextPage={!!hasNextPage}
      isFetchingNextPage={isFetchingNextPage}
      fetchNextPage={fetchNextPage}
      empty={<PostListMessage message={t('empty')} />}
      error={<PostListMessage message={t('error')} variant="destructive" />}
      end={
        <div className="py-4 text-center">
          <p className="text-xs text-muted-foreground">{t('end')}</p>
        </div>
      }
    />
  );
}
