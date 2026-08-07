'use client';

import { TagIcon, UsersIcon, WarningCircleIcon } from '@phosphor-icons/react';
import { useTranslations } from 'next-intl';
import Link from 'next/link';

import { useGetProjectRecommendations } from '@/api/__generated__/project/project';
import { GetProjectRecommendationsResponseProjectsItemJoinPolicy } from '@/api/__generated__/types/GetProjectRecommendationsResponseProjectsItemJoinPolicy';
import { ProjectAvatar } from '@/components/feature/project/avatar';
import { Button } from '@/components/ui/button';
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

const SKELETON_COUNT = 6;

function useProjectMeta() {
  const t = useTranslations('pages.explore.projects');

  const spaceLabel = (
    isPublic: boolean,
    joinPolicy: GetProjectRecommendationsResponseProjectsItemJoinPolicy,
  ) => {
    if (!isPublic) {
      return t('space.team');
    }

    return joinPolicy ===
      GetProjectRecommendationsResponseProjectsItemJoinPolicy.Open
      ? t('space.community')
      : t('space.showcase');
  };

  const joinLabel = (
    joinPolicy: GetProjectRecommendationsResponseProjectsItemJoinPolicy,
  ) => {
    switch (joinPolicy) {
      case GetProjectRecommendationsResponseProjectsItemJoinPolicy.Open:
        return t('join.open');
      case GetProjectRecommendationsResponseProjectsItemJoinPolicy.Request:
        return t('join.request');
      default:
        return t('join.invite');
    }
  };

  return { spaceLabel, joinLabel };
}

export default function ExploreProjects() {
  const t = useTranslations('pages.explore.projects');
  const { spaceLabel, joinLabel } = useProjectMeta();

  const { data, isPending, isError, isFetching, refetch } =
    useGetProjectRecommendations();

  if (isPending) {
    return (
      <ul className="divide-hairline divide-y">
        {Array.from({ length: SKELETON_COUNT }).map((_, index) => (
          <li key={index} className="flex items-center gap-4 py-4">
            <Skeleton className="size-11 shrink-0 rounded-lg" />
            <div className="flex-1 space-y-2">
              <Skeleton className="h-4 w-40" />
              <Skeleton className="h-3 w-64" />
              <Skeleton className="h-3 w-32" />
            </div>
            <Skeleton className="h-12 w-14 shrink-0 rounded-lg" />
          </li>
        ))}
      </ul>
    );
  }

  if (isError) {
    return (
      <Empty className="min-h-80">
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

  const projects = data?.data.projects ?? [];

  if (projects.length === 0) {
    return (
      <Empty className="min-h-80">
        <EmptyTitle>{t('empty.title')}</EmptyTitle>
        <EmptyDescription>{t('empty.description')}</EmptyDescription>
      </Empty>
    );
  }

  return (
    <ol className="divide-hairline divide-y">
      {projects.map((project, index) => (
        <li key={project.handle}>
          <Link
            href={ROUTES.PROJECT(project.handle)}
            className="group hover:bg-muted/50 -mx-3 flex items-center gap-4 rounded-lg px-3 py-4 transition-colors"
          >
            <ProjectAvatar
              name={project.name}
              iconUrl={project.iconUrl}
              size="lg"
              className="size-11 shrink-0 text-lg"
            />

            <div className="min-w-0 flex-1 space-y-1">
              <p className="truncate font-semibold group-hover:underline">
                {t('rank', { rank: index + 1 })} {project.name}
              </p>

              <p className="text-muted-foreground truncate text-sm">
                {project.description || t('no-description')}
              </p>

              <div className="text-muted-foreground flex min-w-0 items-center gap-1.5 text-xs">
                <TagIcon className="size-3.5 shrink-0" />
                <span className="truncate">
                  {spaceLabel(project.isPublic, project.joinPolicy)}
                </span>
                <span
                  aria-hidden
                  className="bg-muted-foreground/60 size-1 shrink-0 rounded-full"
                />
                <span className="truncate">
                  {joinLabel(project.joinPolicy)}
                </span>
              </div>
            </div>

            <div
              className="border-hairline text-muted-foreground group-hover:border-hairline-strong flex w-14 shrink-0 flex-col items-center gap-0.5 rounded-lg border px-2 py-1.5 transition-colors"
              title={t('follower-count', { count: project.followerCount })}
            >
              <UsersIcon className="size-4" />
              <span className="text-foreground text-sm font-medium">
                {project.followerCount}
              </span>
            </div>
          </Link>
        </li>
      ))}
    </ol>
  );
}
