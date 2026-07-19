'use client';

import { useTranslations } from 'next-intl';

import { useGetProjectRecommendations } from '@/api/__generated__/project/project';
import { ProjectCard } from '@/components/feature/project/card';
import { Empty, EmptyDescription, EmptyTitle } from '@/components/ui/empty';
import { Skeleton } from '@/components/ui/skeleton';

const SKELETON_COUNT = 6;

export default function ExploreProjects() {
  const t = useTranslations('pages.explore.projects');

  const { data, isPending } = useGetProjectRecommendations();

  const projects = data?.data.projects ?? [];

  if (isPending) {
    return (
      <div className="grid grid-cols-1 gap-4 sm:grid-cols-2 lg:grid-cols-3">
        {Array.from({ length: SKELETON_COUNT }).map((_, index) => (
          <Skeleton key={index} className="h-48 rounded-lg" />
        ))}
      </div>
    );
  }

  if (projects.length === 0) {
    return (
      <Empty className="min-h-80">
        <EmptyTitle>{t('empty.title')}</EmptyTitle>
        <EmptyDescription>{t('empty.description')}</EmptyDescription>
      </Empty>
    );
  }

  return (
    <div className="grid grid-cols-1 gap-4 sm:grid-cols-2 lg:grid-cols-3">
      {projects.map((project) => (
        <ProjectCard
          key={project.handle}
          name={project.name}
          handle={project.handle}
          iconUrl={project.iconUrl}
          description={project.description}
        />
      ))}
    </div>
  );
}
