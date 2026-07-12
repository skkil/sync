import { CheckCircleIcon, ClockIcon } from '@phosphor-icons/react';
import { useTranslations } from 'next-intl';

import { Badge } from '@/components/ui/badge';
import { LinkButton } from '@/components/ui/button';
import { Card } from '@/components/ui/card';
import ROUTES from '@/util/routes';

import { ProjectAvatar } from './avatar';

interface ProjectCardProps {
  name: string;
  handle: string;
  iconUrl?: string | null;
  description?: string | null;
  role?: 'admin' | 'member';
  memberCount?: number;
  freshPercent?: number;
  toReviewCount?: number;
  unansweredCount?: number;
}

function ProjectCard({
  name,
  handle,
  iconUrl,
  description,
  role,
  memberCount,
  freshPercent,
  toReviewCount,
  unansweredCount,
}: ProjectCardProps) {
  const t = useTranslations('components.project.card');
  const isFresh = freshPercent !== undefined && freshPercent >= 85;

  return (
    <Card className="gap-4 p-5">
      <div className="flex items-start justify-between gap-3">
        <div className="flex min-w-0 items-center gap-3">
          <ProjectAvatar
            name={name}
            iconUrl={iconUrl}
            size="lg"
            className="size-10 text-lg"
          />
          <div className="min-w-0">
            <p className="truncate font-semibold">{name}</p>
            {memberCount !== undefined && (
              <p className="text-muted-foreground text-xs">
                {t('memberCount', { count: memberCount })}
              </p>
            )}
          </div>
        </div>

        {role && (
          <Badge variant="outline" className="shrink-0 font-normal">
            {role === 'admin' ? t('role.admin') : t('role.member')}
          </Badge>
        )}
      </div>

      {description && (
        <p className="text-muted-foreground line-clamp-2 text-sm">
          {description}
        </p>
      )}

      {freshPercent !== undefined &&
        toReviewCount !== undefined &&
        unansweredCount !== undefined && (
          <div className="border-hairline flex items-center justify-between border-t pt-4 text-xs">
            <div className="flex items-center gap-1">
              {isFresh ? (
                <CheckCircleIcon weight="fill" className="text-success-text" />
              ) : (
                <ClockIcon weight="fill" className="text-warning-text" />
              )}
              <span
                className={isFresh ? 'text-success-text' : 'text-warning-text'}
              >
                {freshPercent}%
              </span>
              <span className="text-muted-foreground">{t('fresh')}</span>
            </div>

            <div className="text-muted-foreground">
              <span className="text-foreground font-medium">
                {toReviewCount}
              </span>{' '}
              {t('toReview')}
            </div>

            <div className="text-muted-foreground">
              <span className="text-foreground font-medium">
                {unansweredCount}
              </span>{' '}
              {t('unanswered')}
            </div>
          </div>
        )}

      <LinkButton
        href={ROUTES.PROJECT(handle)}
        size="sm"
        variant="secondary"
        className="w-full"
      >
        {t('open')}
      </LinkButton>
    </Card>
  );
}

export { ProjectCard };
