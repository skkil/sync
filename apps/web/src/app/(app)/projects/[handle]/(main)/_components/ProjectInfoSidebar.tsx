'use client';

import {
  EyeIcon,
  GlobeIcon,
  LockSimpleIcon,
  UsersIcon,
} from '@phosphor-icons/react';
import { useTranslations } from 'next-intl';
import Link from 'next/link';
import type { ReactNode } from 'react';

import {
  useGetProjectByHandle,
  useGetProjectTeammates,
} from '@/api/__generated__/project/project';
import { GetProjectResponseSummaryJoinPolicy } from '@/api/__generated__/types';
import {
  Avatar,
  AvatarFallback,
  AvatarGroup,
  AvatarImage,
} from '@/components/ui/avatar';
import { Button } from '@/components/ui/button';
import { Card, CardContent } from '@/components/ui/card';
import { Skeleton } from '@/components/ui/skeleton';
import {
  Tooltip,
  TooltipContent,
  TooltipTrigger,
} from '@/components/ui/tooltip';
import { toSafeHttpUrl } from '@/lib/utils';
import ROUTES from '@/util/routes';

import AddTeammatePopover from '../../posts/_components/AddTeammatePopover';

const MAX_VISIBLE_MEMBERS = 5;

interface ProjectInfoSidebarProps {
  handle: string;
}

export default function ProjectInfoSidebar({
  handle,
}: ProjectInfoSidebarProps) {
  return (
    <div className="space-y-4">
      <AboutCard handle={handle} />
      <ContributorsCard handle={handle} />
    </div>
  );
}

function AboutCard({ handle }: ProjectInfoSidebarProps) {
  const t = useTranslations('pages.projects.project.sidebar.about');
  const { data, isPending } = useGetProjectByHandle(handle);
  const {
    data: teammatesData,
    isPending: isTeammatesPending,
    isError: isTeammatesError,
  } = useGetProjectTeammates(handle);

  if (isPending || !data) {
    return (
      <Card>
        <CardContent className="space-y-3">
          <Skeleton className="h-4 w-full" />
          <Skeleton className="h-4 w-3/4" />
          <Skeleton className="h-4 w-1/2" />
        </CardContent>
      </Card>
    );
  }

  const { summary } = data.data;
  const websiteUrl = toSafeHttpUrl(summary.website);

  const joinPolicyLabel =
    summary.joinPolicy === GetProjectResponseSummaryJoinPolicy.Open
      ? t('join-policy.open')
      : summary.joinPolicy === GetProjectResponseSummaryJoinPolicy.Request
        ? t('join-policy.request')
        : t('join-policy.invite');

  return (
    <Card>
      <CardContent className="space-y-3">
        <h2 className="text-sm font-semibold">{t('heading')}</h2>

        <p className="text-muted-foreground text-sm">
          {summary.description || t('description-empty')}
        </p>

        {websiteUrl && (
          <a
            href={websiteUrl}
            target="_blank"
            rel="noreferrer noopener"
            className="text-primary flex items-center gap-1.5 text-sm hover:underline"
          >
            <GlobeIcon className="size-4 shrink-0" />
            <span className="truncate">{websiteUrl}</span>
          </a>
        )}

        <div className="text-muted-foreground flex items-center gap-1.5 text-xs">
          {summary.isPublic ? (
            <GlobeIcon className="size-3.5 shrink-0" />
          ) : (
            <LockSimpleIcon className="size-3.5 shrink-0" />
          )}
          {summary.isPublic ? t('visibility.public') : t('visibility.private')}
          {' · '}
          {joinPolicyLabel}
        </div>

        <div className="flex gap-4 border-t pt-3">
          <StatItem
            icon={<EyeIcon className="size-3.5" />}
            count={summary.followerCount}
            label={t('stats.followers')}
          />
          {!isTeammatesError && (
            <StatItem
              icon={<UsersIcon className="size-3.5" />}
              count={
                isTeammatesPending ? (
                  <Skeleton className="h-4 w-5" />
                ) : (
                  teammatesData.data.teammates.length
                )
              }
              label={t('stats.members')}
            />
          )}
        </div>
      </CardContent>
    </Card>
  );
}

function StatItem({
  icon,
  count,
  label,
}: {
  icon: ReactNode;
  count: ReactNode;
  label: ReactNode;
}) {
  return (
    <div className="text-muted-foreground flex items-center gap-1.5 text-sm">
      {icon}
      <span className="text-foreground font-semibold">{count}</span>
      <span>{label}</span>
    </div>
  );
}

function ContributorsCard({ handle }: ProjectInfoSidebarProps) {
  const t = useTranslations('pages.projects.project.sidebar.contributors');
  const { data, isPending, isError } = useGetProjectTeammates(handle);

  if (isError) {
    return null;
  }

  const teammates = data?.data.teammates ?? [];
  const visibleTeammates = teammates.slice(0, MAX_VISIBLE_MEMBERS);
  const hiddenCount = teammates.length - visibleTeammates.length;

  return (
    <Card className="relative">
      <Link
        href={ROUTES.PROJECT_SETTINGS_TEAMMATES(handle)}
        aria-label={t('heading')}
        className="focus-visible:ring-ring absolute inset-0 rounded-lg focus-visible:ring-2 focus-visible:outline-none"
      />
      <CardContent className="pointer-events-none relative z-10 space-y-3">
        <div className="flex items-center justify-between">
          <h2 className="text-sm font-semibold">
            {t('heading')}
            {!isPending && ` · ${teammates.length}`}
          </h2>

          <div className="pointer-events-auto">
            <AddTeammatePopover
              projectHandle={handle}
              trigger={
                <Button variant="link" size="xs" className="h-auto p-0">
                  {t('invite')}
                </Button>
              }
            />
          </div>
        </div>

        {isPending ? (
          <div className="flex -space-x-2">
            {Array.from({ length: 4 }).map((_, index) => (
              <Skeleton
                key={index}
                className="ring-background size-8 rounded-full ring-2"
              />
            ))}
          </div>
        ) : (
          <AvatarGroup>
            {visibleTeammates.map(({ user }) => (
              <Tooltip key={user.handle}>
                <TooltipTrigger asChild>
                  <Avatar size="sm">
                    <AvatarImage
                      src={user.profileImageUrl ?? undefined}
                      alt={user.name}
                    />
                    <AvatarFallback>{user.name.charAt(0)}</AvatarFallback>
                  </Avatar>
                </TooltipTrigger>
                <TooltipContent>{user.name}</TooltipContent>
              </Tooltip>
            ))}
            {hiddenCount > 0 && (
              <div className="bg-muted text-muted-foreground ring-background relative flex size-8 shrink-0 items-center justify-center rounded-full text-xs ring-2">
                +{hiddenCount}
              </div>
            )}
          </AvatarGroup>
        )}
      </CardContent>
    </Card>
  );
}
