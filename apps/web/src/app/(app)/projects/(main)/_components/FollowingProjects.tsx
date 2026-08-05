'use client';

import { useTranslations } from 'next-intl';

import { useGetFollowedProjects } from '@/api/__generated__/project/project';
import { ProjectAvatar } from '@/components/feature/project/avatar';
import { useUnfollowProject } from '@/components/feature/project/hooks/useFollowProject';
import { Button, LinkButton } from '@/components/ui/button';
import { Skeleton } from '@/components/ui/skeleton';
import { useSession } from '@/lib/auth/client';
import ROUTES from '@/util/routes';

function FollowingProjectsSkeleton() {
  return (
    <div className="grid grid-cols-1 gap-3 sm:grid-cols-2 lg:grid-cols-3">
      {Array.from({ length: 3 }).map((_, index) => (
        <Skeleton key={index} className="h-16 w-full rounded-lg" />
      ))}
    </div>
  );
}

export default function FollowingProjects() {
  const t = useTranslations('pages.projects.list.following');
  const { data: session } = useSession();
  const { mutate: unfollowProject, variables: unfollowVariables } =
    useUnfollowProject();

  const { data: projectsData, isPending } = useGetFollowedProjects(
    session?.user.handle || '',
    {
      query: {
        enabled: !!session?.user.handle,
      },
    },
  );

  if (isPending) {
    return <FollowingProjectsSkeleton />;
  }

  const projects = projectsData?.data.projects ?? [];

  if (projects.length === 0) {
    return null;
  }

  return (
    <div className="grid grid-cols-1 gap-3 sm:grid-cols-2 lg:grid-cols-3">
      {projects.map((project) => (
        <div
          key={project.handle}
          className="border-hairline bg-card flex items-center justify-between gap-3 rounded-lg border p-4"
        >
          <LinkButton
            href={ROUTES.PROJECT(project.handle)}
            variant="link"
            className="min-w-0 h-auto justify-start gap-3 p-0 text-foreground hover:no-underline"
          >
            <ProjectAvatar name={project.name} iconUrl={project.iconUrl} />
            <span className="flex min-w-0 flex-col items-start text-left">
              <span className="truncate text-sm font-medium">
                {project.name}
              </span>
              <span className="text-muted-foreground truncate text-xs">
                {project.description ??
                  t('follower-count', { count: project.followerCount })}
              </span>
            </span>
          </LinkButton>

          <Button
            size="sm"
            variant="outline"
            className="shrink-0"
            disabled={unfollowVariables?.handle === project.handle}
            onClick={() => unfollowProject({ handle: project.handle })}
          >
            {t('unfollow')}
          </Button>
        </div>
      ))}
    </div>
  );
}
