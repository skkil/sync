'use client';

import { useTranslations } from 'next-intl';

import { useGetDraftPostsInfinite } from '@/api/__generated__/post/post';
import PostList from '@/components/feature/post/viewer/PostList';
import PostListMessage from '@/components/feature/post/viewer/error/PostListMessage';
import { toPostSummary } from '@/components/feature/post/viewer/types';
import { LinkButton } from '@/components/ui/button';
import ROUTES from '@/util/routes';

const PAGE_SIZE = '30';

interface ProjectDraftsProps {
  handle: string;
}

export default function ProjectDrafts({ handle }: ProjectDraftsProps) {
  const t = useTranslations('pages.projects.project.drafts');

  const {
    data,
    fetchNextPage,
    hasNextPage,
    isError,
    isFetchingNextPage,
    isPending,
  } = useGetDraftPostsInfinite(
    { first: PAGE_SIZE, projectHandle: handle },
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

  const drafts =
    data?.pages.flatMap((page) => page.data.posts?.nodes ?? []) ?? [];

  return (
    <section className="space-y-3">
      <div className="flex flex-wrap items-center justify-between gap-3">
        <h1 className="text-xl font-semibold">{t('heading')}</h1>

        <LinkButton href={ROUTES.NEW_PROJECT_POST(handle)} variant="outline">
          {t('new-post')}
        </LinkButton>
      </div>

      <PostList
        items={drafts.map((draft) => toPostSummary(draft.content))}
        isPending={isPending}
        isError={isError}
        hasNextPage={!!hasNextPage}
        isFetchingNextPage={isFetchingNextPage}
        fetchNextPage={fetchNextPage}
        empty={<PostListMessage message={t('empty')} />}
        error={<PostListMessage message={t('error')} variant="destructive" />}
      />
    </section>
  );
}
