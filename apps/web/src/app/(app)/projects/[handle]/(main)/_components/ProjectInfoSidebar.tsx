'use client';

import { UserPlusIcon } from '@phosphor-icons/react';
import { useTranslations } from 'next-intl';
import Link from 'next/link';

import { useGetPostsByProject } from '@/api/__generated__/post/post';
import { useGetProjectTeammates } from '@/api/__generated__/project/project';
import { useGetProjectTags } from '@/api/__generated__/tag/tag';
import { PostType } from '@/components/feature/post/types/post';
import {
  Avatar,
  AvatarFallback,
  AvatarGroup,
  AvatarImage,
} from '@/components/ui/avatar';
import { Badge } from '@/components/ui/badge';
import { Button } from '@/components/ui/button';
import { Card, CardContent } from '@/components/ui/card';
import { Skeleton } from '@/components/ui/skeleton';
import {
  Tooltip,
  TooltipContent,
  TooltipTrigger,
} from '@/components/ui/tooltip';
import ROUTES from '@/util/routes';

import AddTeammatePopover from '../../posts/_components/AddTeammatePopover';

const MAX_VISIBLE_MEMBERS = 5;
const MAX_TOP_TAGS = 5;
const MAX_OPEN_QUESTIONS = 2;
const OPEN_QUESTIONS_PAGE_SIZE = '10';

interface ProjectInfoSidebarProps {
  handle: string;
}

export default function ProjectInfoSidebar({
  handle,
}: ProjectInfoSidebarProps) {
  return (
    <div className="space-y-4">
      <MembersWidget handle={handle} />
      <OpenQuestionsWidget handle={handle} />
      <TopTagsWidget handle={handle} />
    </div>
  );
}

function MembersWidget({ handle }: ProjectInfoSidebarProps) {
  const t = useTranslations('pages.projects.project.sidebar.members');
  const { data, isPending } = useGetProjectTeammates(handle);

  const teammates = data?.data.teammates ?? [];
  const visibleTeammates = teammates.slice(0, MAX_VISIBLE_MEMBERS);
  const hiddenCount = teammates.length - visibleTeammates.length;

  return (
    <Card className="relative transition-colors hover:bg-muted/30">
      <Link
        href={ROUTES.PROJECT_SETTINGS_TEAMMATES(handle)}
        aria-label={t('manage')}
        className="focus-visible:ring-ring absolute inset-0 rounded-lg focus-visible:ring-2 focus-visible:outline-none"
      />
      <CardContent className="pointer-events-none relative z-10 space-y-4">
        <div className="flex items-center justify-between">
          <h2 className="text-sm font-semibold">{t('heading')}</h2>
          <span className="text-muted-foreground text-xs">
            {teammates.length}
          </span>
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
                  <Link
                    href={ROUTES.PROJECT_SETTINGS_TEAMMATES(handle)}
                    className="pointer-events-auto rounded-full focus-visible:outline-none focus-visible:ring-2 focus-visible:ring-ring"
                  >
                    <Avatar size="sm">
                      <AvatarImage
                        src={user.profileImageUrl ?? undefined}
                        alt={user.name}
                      />
                      <AvatarFallback>{user.name.charAt(0)}</AvatarFallback>
                    </Avatar>
                  </Link>
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

        <div className="pointer-events-auto">
          <AddTeammatePopover
            projectHandle={handle}
            trigger={
              <Button variant="outline" className="w-full">
                <UserPlusIcon />
                {t('invite')}
              </Button>
            }
          />
        </div>
      </CardContent>
    </Card>
  );
}

function OpenQuestionsWidget({ handle }: ProjectInfoSidebarProps) {
  const t = useTranslations('pages.projects.project.sidebar.open-questions');
  const { data, isPending } = useGetPostsByProject(handle, {
    type: PostType.QUESTION,
    first: OPEN_QUESTIONS_PAGE_SIZE,
  });

  const nodes = data?.data.posts?.nodes ?? [];
  const openQuestions = nodes.filter((node) => !node.content.resolved);
  const visibleQuestions = openQuestions.slice(0, MAX_OPEN_QUESTIONS);

  return (
    <Card>
      <CardContent className="space-y-3">
        <div className="flex items-center justify-between">
          <h2 className="text-sm font-semibold">{t('heading')}</h2>
          {!isPending && (
            <Badge color={openQuestions.length > 0 ? 'danger' : 'default'}>
              {openQuestions.length}
            </Badge>
          )}
        </div>

        {isPending ? (
          <div className="space-y-2">
            <Skeleton className="h-4 w-full" />
            <Skeleton className="h-4 w-3/4" />
          </div>
        ) : visibleQuestions.length === 0 ? (
          <p className="text-muted-foreground text-xs">{t('empty')}</p>
        ) : (
          <ul className="space-y-2">
            {visibleQuestions.map((node) => (
              <li key={node.content.id}>
                <Link
                  href={ROUTES.PROJECT_POST(handle, node.content.slug)}
                  className="text-sm hover:underline"
                >
                  {node.content.title}
                </Link>
              </li>
            ))}
          </ul>
        )}
      </CardContent>
    </Card>
  );
}

function TopTagsWidget({ handle }: ProjectInfoSidebarProps) {
  const t = useTranslations('pages.projects.project.sidebar.top-tags');
  const { data, isPending } = useGetProjectTags(handle);

  const tags = [...(data?.data.tags ?? [])]
    .sort((a, b) => b.postCount - a.postCount)
    .slice(0, MAX_TOP_TAGS);

  return (
    <Card>
      <CardContent className="space-y-3">
        <h2 className="text-sm font-semibold">{t('heading')}</h2>

        {isPending ? (
          <div className="flex flex-wrap gap-1.5">
            {Array.from({ length: 4 }).map((_, index) => (
              <Skeleton key={index} className="h-6 w-16 rounded-full" />
            ))}
          </div>
        ) : tags.length === 0 ? (
          <p className="text-muted-foreground text-xs">{t('empty')}</p>
        ) : (
          <div className="flex flex-wrap gap-1.5">
            {tags.map((tag) => (
              <Link
                key={tag.id}
                href={ROUTES.PROJECT_TAG(handle, String(tag.id))}
              >
                <Badge variant="secondary">#{tag.name}</Badge>
              </Link>
            ))}
          </div>
        )}
      </CardContent>
    </Card>
  );
}
