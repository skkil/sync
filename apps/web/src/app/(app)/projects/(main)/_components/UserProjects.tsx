'use client';

import { FolderSimpleIcon } from '@phosphor-icons/react';
import { useTranslations } from 'next-intl';

import { useGetProjectsByUser } from '@/api/__generated__/project/project';
import { ProjectCard } from '@/components/feature/project/card';
import { LinkButton } from '@/components/ui/button';
import {
  Empty,
  EmptyContent,
  EmptyDescription,
  EmptyHeader,
  EmptyMedia,
  EmptyTitle,
} from '@/components/ui/empty';
import { Skeleton } from '@/components/ui/skeleton';
import { useSession } from '@/lib/auth/client';
import ROUTES from '@/util/routes';

// TODO: 멤버 수, 역할, 활동 지표를 반환하는 API가 추가되면 실제 데이터로 교체합니다.
function mockProjectStats(handle: string) {
  let hash = 0;
  for (const char of handle) {
    hash = (hash * 31 + char.charCodeAt(0)) % 1000;
  }

  return {
    role: hash % 5 === 0 ? ('admin' as const) : ('member' as const),
    memberCount: 4 + (hash % 40),
    freshPercent: 60 + (hash % 40),
    toReviewCount: hash % 8,
    unansweredCount: hash % 5,
  };
}

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
  const t = useTranslations('pages.projects.list.empty');
  const { data: session } = useSession();

  const { data: projectsData, isPending } = useGetProjectsByUser(
    session?.user.handle || '',
    {
      query: {
        enabled: !!session?.user.handle,
      },
    },
  );

  if (isPending) {
    return <UserProjectsSkeleton />;
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
          <EmptyTitle>{t('title')}</EmptyTitle>
          <EmptyDescription>{t('description')}</EmptyDescription>
        </EmptyHeader>
        <EmptyContent>
          <LinkButton href={ROUTES.NEW_PROJECT()} size="sm">
            {t('create')}
          </LinkButton>
        </EmptyContent>
      </Empty>
    );
  }

  return (
    <div className="grid grid-cols-1 gap-4 sm:grid-cols-2 lg:grid-cols-3">
      {projects.map((project) => {
        const stats = mockProjectStats(project.handle);

        return (
          <ProjectCard
            key={project.handle}
            name={project.name}
            handle={project.handle}
            iconUrl={project.iconUrl}
            description={project.description}
            {...stats}
          />
        );
      })}
    </div>
  );
}
