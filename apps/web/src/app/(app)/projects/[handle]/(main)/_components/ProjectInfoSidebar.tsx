'use client';

import {
  useGetFollowedProjects,
  useGetProjectByHandle,
} from '@/api/__generated__/project/project';
import { ProjectAvatar } from '@/components/feature/project/avatar';
import {
  useFollowProject,
  useUnfollowProject,
} from '@/components/feature/project/hooks/useFollowProject';
import { Button } from '@/components/ui/button';
import { Skeleton } from '@/components/ui/skeleton';
import { useRequireAuth } from '@/hooks/use-require-auth';
import { isAuthenticated } from '@/lib/auth';
import { useSession } from '@/lib/auth/client';

interface ProjectInfoSidebarProps {
  handle: string;
}

export default function ProjectInfoSidebar({
  handle,
}: ProjectInfoSidebarProps) {
  const { data: session } = useSession();
  const { requireAuth } = useRequireAuth();

  const { data, isPending } = useGetProjectByHandle(handle);

  const { data: followedProjectsData } = useGetFollowedProjects(
    session?.user.handle || '',
    {
      query: {
        enabled: isAuthenticated(session),
      },
    },
  );

  const { mutate: followProject, isPending: isFollowPending } =
    useFollowProject();
  const { mutate: unfollowProject, isPending: isUnfollowPending } =
    useUnfollowProject();

  if (isPending) {
    return <ProjectInfoSidebarSkeleton />;
  }

  if (!data) {
    return null;
  }

  const { summary } = data.data;

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
    <div className="flex flex-col items-center gap-4 rounded-lg border p-6 text-center">
      <ProjectAvatar
        name={summary.name}
        iconUrl={summary.iconUrl}
        size="lg"
        className="size-16 rounded-2xl text-2xl"
      />

      <div>
        <p className="text-lg font-semibold">{summary.name}</p>
        <p className="text-muted-foreground text-sm">@{summary.handle}</p>
      </div>

      <p className="text-sm text-muted-foreground">
        {summary.description || '설명이 없습니다.'}
      </p>

      <Button
        className="w-full"
        variant={isFollowing ? 'outline' : 'default'}
        disabled={isFollowPending || isUnfollowPending}
        onClick={handleFollowToggle}
      >
        {isFollowing ? '팔로잉' : '팔로우'}
      </Button>
    </div>
  );
}

function ProjectInfoSidebarSkeleton() {
  return (
    <div className="flex flex-col items-center gap-4 rounded-lg border p-6">
      <Skeleton className="size-16 rounded-2xl" />
      <div className="flex flex-col items-center gap-2">
        <Skeleton className="h-5 w-32" />
        <Skeleton className="h-4 w-20" />
      </div>
      <Skeleton className="h-4 w-full" />
      <Skeleton className="h-9 w-full" />
    </div>
  );
}
