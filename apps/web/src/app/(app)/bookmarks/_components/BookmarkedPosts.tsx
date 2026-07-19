'use client';

import { BookmarkSimpleIcon } from '@phosphor-icons/react';
import { useTranslations } from 'next-intl';

import { useGetBookmarkedPostsInfinite } from '@/api/__generated__/bookmark/bookmark';
import PostList from '@/components/feature/post/viewer/PostList';
import { toPostSummary } from '@/components/feature/post/viewer/types';
import {
  Empty,
  EmptyDescription,
  EmptyHeader,
  EmptyMedia,
  EmptyTitle,
} from '@/components/ui/empty';

const BOOKMARKED_POST_PAGE_SIZE = '30';

export default function BookmarkedPosts() {
  const t = useTranslations('pages.bookmarks');

  const { data, fetchNextPage, hasNextPage, isFetchingNextPage, isPending } =
    useGetBookmarkedPostsInfinite(
      {
        first: BOOKMARKED_POST_PAGE_SIZE,
        after: '',
      },
      {
        query: {
          getNextPageParam: (lastPage) => {
            const posts = lastPage.data.posts;
            return posts?.pageInfo.hasNextPage
              ? posts.pageInfo.endCursor
              : undefined;
          },
        },
      },
    );

  const posts =
    data?.pages.flatMap((page) => page.data.posts?.nodes ?? []) ?? [];

  return (
    <div className="space-y-4">
      <div className="space-y-1">
        <h1 className="text-2xl font-semibold">{t('title')}</h1>
        <p className="text-sm text-muted-foreground">{t('description')}</p>
      </div>

      <PostList
        items={posts.map((post) => toPostSummary(post.content))}
        isPending={isPending}
        hasNextPage={!!hasNextPage}
        isFetchingNextPage={isFetchingNextPage}
        fetchNextPage={fetchNextPage}
        empty={
          <Empty className="min-h-80">
            <EmptyMedia variant="icon">
              <BookmarkSimpleIcon />
            </EmptyMedia>
            <EmptyHeader>
              <EmptyTitle>{t('empty.title')}</EmptyTitle>
              <EmptyDescription>{t('empty.description')}</EmptyDescription>
            </EmptyHeader>
          </Empty>
        }
      />
    </div>
  );
}
