'use client';

import { FolderSimpleIcon, WarningCircleIcon } from '@phosphor-icons/react';
import { useTranslations } from 'next-intl';

import { useGetMyProjects } from '@/api/__generated__/project/project';
import { ProjectCard } from '@/components/feature/project/card';
import { Button, LinkButton } from '@/components/ui/button';
import {
  Empty,
  EmptyContent,
  EmptyDescription,
  EmptyHeader,
  EmptyMedia,
  EmptyTitle,
} from '@/components/ui/empty';
import { Skeleton } from '@/components/ui/skeleton';
import ROUTES from '@/util/routes';

function UserProjectsSkeleton() {
  return (
    <div className="grid grid-cols-1 gap-4 sm:grid-cols-2 lg:grid-cols-3">
      {Array.from({ length: 3 }).map((_, index) => (
        <Skeleton key={index} className="h-56 w-full rounded-xl" />
      ))}
    </div>
  );
}

export default function UserProjects() {
  const t = useTranslations('pages.projects.list');
  const {
    data: projectsData,
    isPending,
    isError,
    isFetching,
    refetch,
  } = useGetMyProjects({
    query: { staleTime: 0 },
  });

  if (isPending) {
    return <UserProjectsSkeleton />;
  }

  if (isError) {
    return (
      <Empty>
        <EmptyHeader>
          <EmptyMedia variant="icon">
            <WarningCircleIcon />
          </EmptyMedia>
          <EmptyTitle>{t('error.title')}</EmptyTitle>
          <EmptyDescription>{t('error.description')}</EmptyDescription>
        </EmptyHeader>
        <EmptyContent>
          <Button
            size="sm"
            variant="outline"
            disabled={isFetching}
            onClick={() => void refetch()}
          >
            {t('error.retry')}
          </Button>
        </EmptyContent>
      </Empty>
    );
  }

  if (!projectsData) {
    return null;
  }

  const { projects } = projectsData.data;

  if (projects.length === 0) {
    return (
      <Empty>
        <EmptyHeader>
          <EmptyMedia variant="icon">
            <FolderSimpleIcon />
          </EmptyMedia>
          <EmptyTitle>{t('empty.title')}</EmptyTitle>
          <EmptyDescription>{t('empty.description')}</EmptyDescription>
        </EmptyHeader>
        <EmptyContent>
          <LinkButton href={ROUTES.NEW_PROJECT()} size="sm">
            {t('empty.create')}
          </LinkButton>
        </EmptyContent>
      </Empty>
    );
  }

  return (
    <div className="grid grid-cols-1 gap-4 sm:grid-cols-2 lg:grid-cols-3">
      {projects.map((project) => (
        <ProjectCard
          key={project.handle}
          variant="membership"
          name={project.name}
          handle={project.handle}
          iconUrl={project.iconUrl}
          description={project.description}
          isPublic={project.isPublic}
          joinPolicy={project.joinPolicy}
          followerCount={project.followerCount}
          role={project.role}
          isOwner={project.isOwner}
          memberCount={project.memberCount}
          unresolvedQuestionCount={project.unresolvedQuestionCount}
        />
      ))}
    </div>
  );
}
