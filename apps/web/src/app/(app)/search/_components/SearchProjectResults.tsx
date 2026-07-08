'use client';

import { MagnifyingGlassIcon } from '@phosphor-icons/react';
import { useTranslations } from 'next-intl';

import { useSearchProjects } from '@/api/__generated__/project/project';
import { ProjectCard } from '@/components/feature/project/card';
import {
  Empty,
  EmptyHeader,
  EmptyMedia,
  EmptyTitle,
} from '@/components/ui/empty';
import { Skeleton } from '@/components/ui/skeleton';

interface SearchProjectResultsProps {
  query: string;
  limit?: number;
}

export default function SearchProjectResults({
  query,
  limit,
}: SearchProjectResultsProps) {
  const t = useTranslations('pages.search');
  const { data, isPending } = useSearchProjects(
    { query },
    { query: { enabled: !!query } },
  );

  if (isPending) {
    return (
      <div className="grid grid-cols-2 gap-4 sm:grid-cols-3 md:grid-cols-4">
        {Array.from({ length: 4 }).map((_, index) => (
          <Skeleton key={index} className="h-32 w-full" />
        ))}
      </div>
    );
  }

  const projects = data?.data.projects ?? [];
  const visibleProjects = limit ? projects.slice(0, limit) : projects;

  if (visibleProjects.length === 0) {
    return (
      <Empty className="min-h-60">
        <EmptyMedia variant="icon">
          <MagnifyingGlassIcon />
        </EmptyMedia>
        <EmptyHeader>
          <EmptyTitle>{t('results.no-results', { query })}</EmptyTitle>
        </EmptyHeader>
      </Empty>
    );
  }

  return (
    <div className="grid grid-cols-2 gap-4 sm:grid-cols-3 md:grid-cols-4">
      {visibleProjects.map((project) => (
        <ProjectCard
          key={project.handle}
          name={project.name}
          handle={project.handle}
          iconUrl={project.iconUrl}
        />
      ))}
    </div>
  );
}
