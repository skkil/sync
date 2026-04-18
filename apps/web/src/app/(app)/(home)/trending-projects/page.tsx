'use client';

import { useTranslations } from 'next-intl';

import { useGetTrendingProjectsInfinite } from '@/api/__generated__/project/project';
import { Button } from '@/components/ui/button';

const TRENDING_PROJECTS_PAGE_SIZE = '20';

export default function TrendingProjects() {
  const t = useTranslations('pages.trending-projects');

  const { data, fetchNextPage, hasNextPage, isFetchingNextPage, isLoading } =
    useGetTrendingProjectsInfinite(
      {
        first: TRENDING_PROJECTS_PAGE_SIZE,
      },
      {
        query: {
          getNextPageParam: (lastPage) => {
            const projects = lastPage.data.projects;
            return projects?.pageInfo.hasNextPage
              ? projects.pageInfo.endCursor
              : undefined;
          },
        },
      },
    );

  const projects =
    data?.pages.flatMap((page) => page.data.projects?.nodes ?? []) ?? [];

  if (isLoading) {
    return <div>{t('loading')}</div>;
  }

  if (data && projects.length === 0) {
    return <div>{t('empty')}</div>;
  }

  return (
    <div className="space-y-4">
      {projects.map(({ content: { id, name, description } }) => (
        <div key={id} className="rounded-lg border p-4">
          <h3 className="text-lg font-semibold">{name}</h3>
          <p className="text-sm text-muted-foreground">{description}</p>
        </div>
      ))}

      {hasNextPage && (
        <div className="flex justify-center">
          <Button onClick={() => fetchNextPage()} disabled={isFetchingNextPage}>
            {isFetchingNextPage ? t('loading') : t('load-more')}
          </Button>
        </div>
      )}
    </div>
  );
}
