import { useTranslations } from 'next-intl';

import { Badge } from '@/components/ui/badge';
import { LinkButton } from '@/components/ui/button';
import { Card } from '@/components/ui/card';
import ROUTES from '@/util/routes';

import { ProjectAvatar } from './avatar';

interface ProjectCardBaseProps {
  name: string;
  handle: string;
  iconUrl?: string | null;
  description?: string | null;
  isPublic: boolean;
  joinPolicy: 'OPEN' | 'REQUEST' | 'INVITE';
  followerCount: number;
}

interface ProjectSummaryCardProps extends ProjectCardBaseProps {
  variant: 'summary';
}

interface ProjectMembershipCardProps extends ProjectCardBaseProps {
  variant: 'membership';
  role: 'ADMIN' | 'MEMBER';
  isOwner: boolean;
  memberCount: number;
  unresolvedQuestionCount: number;
}

type ProjectCardProps = ProjectSummaryCardProps | ProjectMembershipCardProps;

function ProjectCard(props: ProjectCardProps) {
  const t = useTranslations('components.project.card');
  const roleLabel =
    props.variant === 'membership'
      ? props.isOwner
        ? t('role.owner')
        : props.role === 'ADMIN'
          ? t('role.admin')
          : t('role.member')
      : null;
  const joinPolicyLabel =
    props.joinPolicy === 'OPEN'
      ? t('joinPolicy.open')
      : props.joinPolicy === 'REQUEST'
        ? t('joinPolicy.request')
        : t('joinPolicy.invite');

  return (
    <Card className="gap-4 p-5">
      <div className="flex items-start justify-between gap-3">
        <div className="flex min-w-0 items-center gap-3">
          <ProjectAvatar
            name={props.name}
            iconUrl={props.iconUrl}
            size="lg"
            className="size-10 text-lg"
          />
          <div className="min-w-0">
            <p className="truncate font-semibold">{props.name}</p>
            {props.variant === 'membership' && (
              <p className="text-muted-foreground text-xs">
                {t('memberCount', { count: props.memberCount })}
              </p>
            )}
          </div>
        </div>

        {roleLabel && (
          <Badge variant="outline" className="shrink-0 font-normal">
            {roleLabel}
          </Badge>
        )}
      </div>

      {props.description && (
        <p className="text-muted-foreground line-clamp-2 text-sm">
          {props.description}
        </p>
      )}

      <div className="flex flex-wrap items-center gap-2">
        <Badge variant="secondary" className="font-normal">
          {props.isPublic ? t('visibility.public') : t('visibility.private')}
        </Badge>
        <Badge variant="outline" className="font-normal">
          {joinPolicyLabel}
        </Badge>
      </div>

      <div className="border-hairline text-muted-foreground flex flex-wrap gap-x-4 gap-y-1 border-t pt-4 text-xs">
        <span>{t('followerCount', { count: props.followerCount })}</span>
        {props.variant === 'membership' && (
          <span>
            {t('unresolvedQuestionCount', {
              count: props.unresolvedQuestionCount,
            })}
          </span>
        )}
      </div>

      <LinkButton
        href={ROUTES.PROJECT(props.handle)}
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
