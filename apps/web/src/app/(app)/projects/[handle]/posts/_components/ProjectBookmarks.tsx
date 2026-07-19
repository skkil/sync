'use client';

import { BookmarkSimpleIcon } from '@phosphor-icons/react';
import { useTranslations } from 'next-intl';

import { useGetBookmarkedPostsInfinite } from '@/api/__generated__/bookmark/bookmark';
import PostList from '@/components/feature/post/viewer/PostList';
import { toPostSummary } from '@/components/feature/post/viewer/types';
import {
  Empty,
  EmptyHeader,
  EmptyMedia,
  EmptyTitle,
} from '@/components/ui/empty';

const BOOKMARKED_POST_PAGE_SIZE = '30';

interface ProjectBookmarksProps {
  handle: string;
}

export default function ProjectBookmarks({ handle }: ProjectBookmarksProps) {
  const t = useTranslations('pages.projects.project.bookmarks');

  const { data, fetchNextPage, hasNextPage, isFetchingNextPage, isPending } =
    useGetBookmarkedPostsInfinite(
      {
        first: BOOKMARKED_POST_PAGE_SIZE,
        after: '',
        projectHandle: handle,
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
    <section className="space-y-3">
      <h1 className="text-xl font-semibold">{t('heading')}</h1>

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
              <EmptyTitle>{t('empty')}</EmptyTitle>
            </EmptyHeader>
          </Empty>
        }
      />
    </section>
  );
}
