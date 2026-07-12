'use client';

import { PencilSimpleIcon, TrashIcon } from '@phosphor-icons/react';
import { useTranslations } from 'next-intl';
import { useParams } from 'next/navigation';
import { toast } from 'sonner';

import {
  useGetProjectByHandle,
  useGetProjectInvitations,
  useGetProjectTeammates,
} from '@/api/__generated__/project/project';
import {
  GetProjectResponseRole,
  GetProjectTeammatesResponseTeammatesItemRole,
} from '@/api/__generated__/types';
import { useCancelProjectInvitation } from '@/components/feature/project/hooks/useProjectInvitation';
import { Avatar, AvatarFallback, AvatarImage } from '@/components/ui/avatar';
import { Badge } from '@/components/ui/badge';
import { Button } from '@/components/ui/button';
import { Separator } from '@/components/ui/separator';
import {
  Table,
  TableBody,
  TableCell,
  TableHead,
  TableHeader,
  TableRow,
} from '@/components/ui/table';

import AddTeammateDropdown from './AddTeammateDropdown';

export default function TeammatesSettingsView() {
  const t = useTranslations('pages.projects.project.settings.teammates');

  const ROLE_LABEL: Record<
    GetProjectTeammatesResponseTeammatesItemRole,
    string
  > = {
    [GetProjectTeammatesResponseTeammatesItemRole.Admin]: t('role.admin'),
    [GetProjectTeammatesResponseTeammatesItemRole.Member]: t('role.member'),
  };

  const { handle } = useParams<{ handle: string }>();
  const { data: teammatesData, isPending: isTeammatesPending } =
    useGetProjectTeammates(handle);
  const { data: projectData } = useGetProjectByHandle(handle);
  const { data: invitationsData } = useGetProjectInvitations(handle);

  const isAdmin = projectData?.data.role === GetProjectResponseRole.Admin;

  const teammates = teammatesData?.data.teammates ?? [];
  const invitations = invitationsData?.data.invitations ?? [];

  return (
    <div className="space-y-6">
      <div className="flex items-center justify-between">
        <div>
          <h2 className="text-lg font-semibold">{t('heading')}</h2>
          <p className="text-sm text-muted-foreground">
            {t('member-count', { count: teammates.length })}
          </p>
        </div>

        {isAdmin && <AddTeammateDropdown projectHandle={handle} />}
      </div>

      <Separator />

      {isTeammatesPending ? (
        <TeammatesTableSkeleton />
      ) : (
        <Table className="border-separate border-spacing-y-1">
          <TableHeader>
            <TableRow className="hover:bg-transparent">
              <TableHead className="border-l-0">
                {t('table.columns.name')}
              </TableHead>
              <TableHead className="border-l-0">
                {t('table.columns.role')}
              </TableHead>
              <TableHead className="w-0 border-l-0" />
            </TableRow>
          </TableHeader>
          <TableBody>
            {teammates.map((teammate) => (
              <TableRow key={teammate.user.handle} className="border-0">
                <TableCell className="border-l-0">
                  <div className="flex items-center gap-3">
                    <Avatar>
                      <AvatarImage
                        src={teammate.user.profileImageUrl ?? undefined}
                      />
                      <AvatarFallback>
                        {teammate.user.name.charAt(0).toUpperCase()}
                      </AvatarFallback>
                    </Avatar>
                    <div>
                      <p className="text-sm font-medium">
                        {teammate.user.name}
                      </p>
                      <p className="text-xs text-muted-foreground">
                        @{teammate.user.handle}
                      </p>
                    </div>
                  </div>
                </TableCell>
                <TableCell className="border-l-0">
                  <Badge variant="outline">{ROLE_LABEL[teammate.role]}</Badge>
                </TableCell>
                <TableCell className="border-l-0">
                  {isAdmin && (
                    <div className="flex items-center justify-end gap-1">
                      <Button
                        variant="ghost"
                        size="icon"
                        aria-label={t('actions.edit-role')}
                        onClick={() => toast.info(t('messages.unsupported'))}
                      >
                        <PencilSimpleIcon className="h-4 w-4" />
                      </Button>
                      <Button
                        variant="ghost"
                        size="icon"
                        aria-label={t('actions.remove')}
                        onClick={() => toast.info(t('messages.unsupported'))}
                      >
                        <TrashIcon className="h-4 w-4" />
                      </Button>
                    </div>
                  )}
                </TableCell>
              </TableRow>
            ))}

            {isAdmin &&
              invitations.map((invitation) => (
                <PendingInvitationRow
                  key={invitation.invitation.id}
                  projectHandle={handle}
                  invitationId={invitation.invitation.id}
                  name={invitation.invitee.name}
                  handle={invitation.invitee.handle}
                />
              ))}
          </TableBody>
        </Table>
      )}
    </div>
  );
}

function PendingInvitationRow({
  projectHandle,
  invitationId,
  name,
  handle,
}: {
  projectHandle: string;
  invitationId: number;
  name: string;
  handle: string;
}) {
  const t = useTranslations('pages.projects.project.settings.teammates');

  const { mutate: cancelInvitation, isPending } = useCancelProjectInvitation();

  const onCancel = () => {
    cancelInvitation(
      {
        handle: projectHandle,
        invitationId: invitationId.toString(),
      },
      {
        onSuccess: () => {
          toast.success(t('messages.cancel-success'));
        },
        onError: () => {
          toast.error(t('messages.cancel-error'));
        },
      },
    );
  };

  return (
    <TableRow className="border-0">
      <TableCell className="border-l-0">
        <div className="flex items-center gap-3">
          <Avatar>
            <AvatarFallback>{name.charAt(0).toUpperCase()}</AvatarFallback>
          </Avatar>
          <div>
            <p className="text-sm font-medium">{name}</p>
            <p className="text-xs text-muted-foreground">@{handle}</p>
          </div>
        </div>
      </TableCell>
      <TableCell className="border-l-0">
        <Badge variant="secondary">{t('status.pending')}</Badge>
      </TableCell>
      <TableCell className="border-l-0">
        <div className="flex items-center justify-end">
          <Button
            variant="ghost"
            size="icon"
            aria-label={t('actions.cancel-invitation')}
            disabled={isPending}
            onClick={onCancel}
          >
            <TrashIcon className="h-4 w-4" />
          </Button>
        </div>
      </TableCell>
    </TableRow>
  );
}

function TeammatesTableSkeleton() {
  return (
    <div className="space-y-3">
      {Array.from({ length: 3 }).map((_, i) => (
        <div key={i} className="flex items-center gap-3">
          <div className="h-8 w-8 rounded-full bg-muted animate-pulse" />
          <div className="space-y-1">
            <div className="h-3 w-24 rounded bg-muted animate-pulse" />
            <div className="h-3 w-16 rounded bg-muted animate-pulse" />
          </div>
        </div>
      ))}
    </div>
  );
}
