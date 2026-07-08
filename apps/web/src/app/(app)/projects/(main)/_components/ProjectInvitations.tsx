'use client';

import { MailboxIcon } from '@phosphor-icons/react';
import { useQueryClient } from '@tanstack/react-query';
import { toast } from 'sonner';

import {
  getGetMyProjectInvitationsQueryOptions,
  getGetProjectTeammatesQueryKey,
  useAcceptProjectInvitation,
  useDeclineProjectInvitation,
  useGetMyProjectInvitations,
} from '@/api/__generated__/project/project';
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

function ProjectInvitationsSkeleton() {
  return (
    <div className="flex flex-col gap-3 pt-4">
      {Array.from({ length: 3 }).map((_, index) => (
        <div
          key={index}
          className="flex items-center justify-between gap-4 rounded-lg border p-4"
        >
          <div className="flex items-center gap-3">
            <Skeleton className="h-10 w-10 rounded-full" />
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

function ProjectInvitationsEmpty() {
  return (
    <Empty className="pt-4">
      <EmptyHeader>
        <EmptyMedia variant="icon">
          <MailboxIcon />
        </EmptyMedia>
        <EmptyTitle>받은 초대가 없습니다</EmptyTitle>
        <EmptyDescription>
          프로젝트 초대를 받으면 이곳에 표시됩니다.
        </EmptyDescription>
      </EmptyHeader>
      <EmptyContent />
    </Empty>
  );
}

export default function ProjectInvitations() {
  const queryClient = useQueryClient();

  const { data: invitationsData, isPending } = useGetMyProjectInvitations();

  const { mutate: acceptInvitation, isPending: isAccepting } =
    useAcceptProjectInvitation();
  const { mutate: declineInvitation, isPending: isDeclining } =
    useDeclineProjectInvitation();

  if (isPending) {
    return <ProjectInvitationsSkeleton />;
  }

  const invitations = invitationsData?.data.invitations ?? [];

  if (invitations.length === 0) {
    return <ProjectInvitationsEmpty />;
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
          toast.success('초대를 수락했습니다.');
        },
        onError: () => {
          toast.error('초대 수락에 실패했습니다.');
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
          toast.success('초대를 거절했습니다.');
        },
        onError: () => {
          toast.error('초대 거절에 실패했습니다.');
        },
      },
    );
  };

  return (
    <div className="flex flex-col gap-3 pt-4">
      {invitations.map((invitation) => (
        <div
          key={invitation.invitation.id}
          className="flex items-center justify-between gap-4 rounded-lg border p-4"
        >
          <div className="flex items-center gap-3">
            <ProjectAvatar
              name={invitation.project.name}
              iconUrl={invitation.project.iconUrl}
              size="lg"
            />
            <div className="flex flex-col">
              <span className="text-sm font-medium">
                {invitation.project.name}
              </span>
              <span className="text-xs text-muted-foreground">
                {invitation.invitation.inviter.name}님이 초대함
              </span>
            </div>
          </div>
          <div className="flex gap-2">
            <Button
              size="sm"
              variant="outline"
              disabled={isAccepting || isDeclining}
              onClick={() => handleDecline(invitation.token)}
            >
              거절
            </Button>
            <Button
              size="sm"
              disabled={isAccepting || isDeclining}
              onClick={() =>
                handleAccept(invitation.token, invitation.project.handle)
              }
            >
              수락
            </Button>
          </div>
        </div>
      ))}
    </div>
  );
}
