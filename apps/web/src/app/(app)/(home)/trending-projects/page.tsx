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
        size: TRENDING_PROJECTS_PAGE_SIZE,
      },
      {
        query: {
          getNextPageParam: (lastPage) => {
            const projects = lastPage.data.projects;
            return projects?.hasNext ? projects.nextCursor : undefined;
          },
        },
      },
    );

  const projects =
    data?.pages.flatMap((page) => page.data.projects?.content ?? []) ?? [];

  if (isLoading) {
    return <div>{t('loading')}</div>;
  }

  if (data && projects.length === 0) {
    return <div>{t('empty')}</div>;
  }

  return (
    <div className="space-y-4">
      {projects.map((project) => (
        <div key={project.id} className="rounded-lg border p-4">
          <h3 className="text-lg font-semibold">{project.name}</h3>
          <p className="text-sm text-muted-foreground">{project.description}</p>
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
