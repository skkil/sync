'use client';

import { EnvelopeSimpleIcon } from '@phosphor-icons/react/dist/ssr';
import { useQueryClient } from '@tanstack/react-query';
import { useTranslations } from 'next-intl';
import { toast } from 'sonner';

import {
  getGetMyProjectInvitationsQueryOptions,
  getGetProjectTeammatesQueryKey,
  useAcceptProjectInvitation,
  useDeclineProjectInvitation,
  useGetMyProjectInvitations,
} from '@/api/__generated__/project/project';
import { ProjectAvatar } from '@/components/feature/project/avatar';
import { Badge } from '@/components/ui/badge';
import { Button } from '@/components/ui/button';
import {
  Empty,
  EmptyDescription,
  EmptyHeader,
  EmptyMedia,
  EmptyTitle,
} from '@/components/ui/empty';
import { Skeleton } from '@/components/ui/skeleton';

function ProjectInvitationsSkeleton() {
  return (
    <div className="flex flex-col gap-3">
      {Array.from({ length: 2 }).map((_, index) => (
        <div
          key={index}
          className="flex items-center justify-between gap-4 rounded-lg border p-4"
        >
          <div className="flex items-center gap-3">
            <Skeleton className="h-10 w-10 rounded-lg" />
            <div className="flex flex-col gap-2">
              <Skeleton className="h-4 w-40" />
              <Skeleton className="h-3 w-28" />
            </div>
          </div>
          <div className="flex gap-2">
            <Skeleton className="h-8 w-16" />
            <Skeleton className="h-8 w-16" />
          </div>
        </div>
      ))}
    </div>
  );
}

export default function ProjectInvitations() {
  const t = useTranslations('pages.projects.invitations');

  const queryClient = useQueryClient();

  const { data: invitationsData, isPending } = useGetMyProjectInvitations();

  const { mutate: acceptInvitation, isPending: isAccepting } =
    useAcceptProjectInvitation();
  const { mutate: declineInvitation, isPending: isDeclining } =
    useDeclineProjectInvitation();

  if (isPending) {
    return (
      <section className="space-y-3">
        <h2 className="text-muted-foreground text-xs font-semibold tracking-wide uppercase">
          {t('heading')}
        </h2>
        <ProjectInvitationsSkeleton />
      </section>
    );
  }

  const invitations = invitationsData?.data.invitations ?? [];

  if (invitations.length === 0) {
    return (
      <Empty>
        <EmptyHeader>
          <EmptyMedia variant="icon">
            <EnvelopeSimpleIcon />
          </EmptyMedia>
          <EmptyTitle>{t('empty.title')}</EmptyTitle>
          <EmptyDescription>{t('empty.description')}</EmptyDescription>
        </EmptyHeader>
      </Empty>
    );
  }

  const invalidateInvitations = async () => {
    await queryClient.invalidateQueries(
      getGetMyProjectInvitationsQueryOptions(),
    );
  };

  const handleAccept = (token: string, projectHandle: string) => {
    acceptInvitation(
      { token },
      {
        onSuccess: async () => {
          await Promise.all([
            invalidateInvitations(),
            queryClient.invalidateQueries({
              queryKey: getGetProjectTeammatesQueryKey(projectHandle),
            }),
          ]);
          toast.success(t('messages.accept-success'));
        },
        onError: () => {
          toast.error(t('messages.accept-error'));
        },
      },
    );
  };

  const handleDecline = (token: string) => {
    declineInvitation(
      { token },
      {
        onSuccess: async () => {
          await invalidateInvitations();
          toast.success(t('messages.decline-success'));
        },
        onError: () => {
          toast.error(t('messages.decline-error'));
        },
      },
    );
  };

  const roleLabel: Record<string, string> = {
    ADMIN: t('role.admin'),
    MEMBER: t('role.member'),
  };

  return (
    <section className="bg-success-tint/40 space-y-4 rounded-xl border border-success-tint p-4">
      <h2 className="flex items-center gap-2 text-xs font-semibold tracking-wide uppercase text-muted-foreground">
        {t('heading')}
        <Badge color="success">{invitations.length}</Badge>
      </h2>

      <div className="grid grid-cols-1 gap-3 lg:grid-cols-2">
        {invitations.map((invitation) => (
          <div
            key={invitation.invitation.id}
            className="bg-card border-hairline flex items-center justify-between gap-4 rounded-lg border p-4"
          >
            <div className="flex min-w-0 items-center gap-3">
              <ProjectAvatar
                name={invitation.project.name}
                iconUrl={invitation.project.iconUrl}
                size="lg"
              />
              <div className="flex min-w-0 flex-col">
                <span className="truncate text-sm font-medium">
                  {invitation.project.name}
                </span>
                <span className="text-muted-foreground truncate text-xs">
                  {t('invited-by', {
                    name: invitation.invitation.inviter.name,
                  })}{' '}
                  ·{' '}
                  {roleLabel[invitation.invitation.role] ??
                    invitation.invitation.role}
                </span>
              </div>
            </div>
            <div className="flex shrink-0 gap-2">
              <Button
                size="sm"
                variant="outline"
                disabled={isAccepting || isDeclining}
                onClick={() => handleDecline(invitation.token)}
              >
                {t('actions.decline')}
              </Button>
              <Button
                size="sm"
                disabled={isAccepting || isDeclining}
                onClick={() =>
                  handleAccept(invitation.token, invitation.project.handle)
                }
              >
                {t('actions.accept')}
              </Button>
            </div>
          </div>
        ))}
      </div>
    </section>
  );
}
