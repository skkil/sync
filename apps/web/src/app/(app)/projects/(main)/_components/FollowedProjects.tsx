'use client';

import { CompassIcon } from '@phosphor-icons/react';

import { useGetFollowedProjects } from '@/api/__generated__/project/project';
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

function FollowedProjectsSkeleton() {
  return (
    <div className="grid grid-cols-1 gap-4 pt-4 sm:grid-cols-2 lg:grid-cols-3">
      {Array.from({ length: 3 }).map((_, index) => (
        <Skeleton key={index} className="h-32 w-full rounded-lg" />
      ))}
    </div>
  );
}

function FollowedProjectsEmpty() {
  return (
    <Empty className="pt-4">
      <EmptyHeader>
        <EmptyMedia variant="icon">
          <CompassIcon />
        </EmptyMedia>
        <EmptyTitle>팔로우 중인 프로젝트가 없습니다</EmptyTitle>
        <EmptyDescription>
          탐색 페이지에서 관심 있는 프로젝트를 팔로우해보세요.
        </EmptyDescription>
      </EmptyHeader>
      <EmptyContent>
        <LinkButton href="/explore" size="sm">
          탐색하기
        </LinkButton>
      </EmptyContent>
    </Empty>
  );
}

export default function FollowedProjects() {
  const { data: session } = useSession();

  const { data: projectsData, isPending } = useGetFollowedProjects(
    session?.user.handle || '',
    {
      query: {
        enabled: !!session?.user.handle,
      },
    },
  );

  if (isPending) {
    return <FollowedProjectsSkeleton />;
  }

  const projects = projectsData?.data.projects ?? [];

  if (projects.length === 0) {
    return <FollowedProjectsEmpty />;
  }

  return (
    <div className="grid grid-cols-1 gap-4 pt-4 sm:grid-cols-2 lg:grid-cols-3">
      {projects.map((project) => (
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
