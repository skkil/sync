'use client';

import { PencilIcon } from '@phosphor-icons/react';
import { useTranslations } from 'next-intl';

import {
  useGetFollowedProjects,
  useGetProjectByHandle,
  useGetProjectTeammates,
} from '@/api/__generated__/project/project';
import { GetProjectResponseRole } from '@/api/__generated__/types';
import { ProjectAvatar } from '@/components/feature/project/avatar';
import {
  useFollowProject,
  useUnfollowProject,
} from '@/components/feature/project/hooks/useFollowProject';
import { Badge } from '@/components/ui/badge';
import { Button, LinkButton } from '@/components/ui/button';
import { Card, CardContent } from '@/components/ui/card';
import { Skeleton } from '@/components/ui/skeleton';
import { useRequireAuth } from '@/hooks/use-require-auth';
import { isAuthenticated } from '@/lib/auth';
import { useSession } from '@/lib/auth/client';
import ROUTES from '@/util/routes';

// TODO: project creation date isn't returned by the API yet — derives a
// stable placeholder year from the handle until that field exists.
function mockCreatedYear(handle: string): number {
  let hash = 0;
  for (const char of handle) {
    hash = (hash * 31 + char.charCodeAt(0)) % 1000;
  }
  return 2022 + (hash % 4);
}

interface ProjectHeaderProps {
  handle: string;
}

export default function ProjectHeader({ handle }: ProjectHeaderProps) {
  const t = useTranslations('pages.projects.project.header');
  const { data: session } = useSession();
  const { requireAuth } = useRequireAuth();

  const { data, isPending } = useGetProjectByHandle(handle);
  const { data: teammatesData } = useGetProjectTeammates(handle);
  const { data: followedProjectsData } = useGetFollowedProjects(
    session?.user.handle || '',
    { query: { enabled: isAuthenticated(session) } },
  );

  const { mutate: followProject, isPending: isFollowPending } =
    useFollowProject();
  const { mutate: unfollowProject, isPending: isUnfollowPending } =
    useUnfollowProject();

  if (isPending || !data) {
    return <ProjectHeaderSkeleton />;
  }

  const { summary, role } = data.data;
  const memberCount = teammatesData?.data.teammates.length ?? 0;
  const isMember = !!role;
  const isFollowing =
    followedProjectsData?.data.projects.some((p) => p.handle === handle) ??
    false;

  const handleFollowToggle = () => {
    if (!requireAuth({ intent: 'follow' })) {
      return;
    }

    if (isFollowing) {
      unfollowProject({ handle });
      return;
    }

    followProject({ handle });
  };

  return (
    <Card>
      <CardContent className="flex flex-col gap-4 sm:flex-row sm:items-start sm:justify-between">
        <div className="flex gap-4">
          <ProjectAvatar
            name={summary.name}
            iconUrl={summary.iconUrl}
            size="lg"
            className="size-16 rounded-2xl text-2xl"
          />

          <div className="space-y-1">
            <div className="flex flex-wrap items-center gap-2">
              <h1 className="text-xl font-semibold">{summary.name}</h1>
              {role === GetProjectResponseRole.Admin && (
                <Badge variant="secondary">{t('role.admin')}</Badge>
              )}
            </div>

            <p className="text-muted-foreground text-sm">
              {summary.description || t('description-empty')}
            </p>

            <p className="text-muted-foreground text-xs">
              {t('meta', {
                count: memberCount,
                year: mockCreatedYear(handle),
              })}
            </p>
          </div>
        </div>

        <div className="flex shrink-0 items-center gap-2">
          {isMember ? (
            <Button variant="outline" disabled>
              {t('status.member')}
            </Button>
          ) : (
            <Button
              variant={isFollowing ? 'outline' : 'default'}
              disabled={isFollowPending || isUnfollowPending}
              onClick={handleFollowToggle}
            >
              {isFollowing ? t('follow.following') : t('follow.follow')}
            </Button>
          )}

          <LinkButton
            href={ROUTES.NEW_PROJECT_POST(handle)}
            onClick={(event) => {
              if (
                !requireAuth({
                  intent: 'write',
                  redirectTo: ROUTES.NEW_PROJECT_POST(handle),
                })
              ) {
                event.preventDefault();
              }
            }}
          >
            <PencilIcon />
            {t('actions.write')}
          </LinkButton>
        </div>
      </CardContent>
    </Card>
  );
}

function ProjectHeaderSkeleton() {
  return (
    <Card>
      <CardContent className="flex items-start gap-4">
        <Skeleton className="size-16 shrink-0 rounded-2xl" />
        <div className="flex-1 space-y-2">
          <Skeleton className="h-6 w-40" />
          <Skeleton className="h-4 w-64" />
          <Skeleton className="h-3 w-32" />
        </div>
      </CardContent>
    </Card>
  );
}
