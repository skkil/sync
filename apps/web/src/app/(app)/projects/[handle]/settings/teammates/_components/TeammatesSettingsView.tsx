'use client';

import { TrashIcon } from '@phosphor-icons/react';
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
  type GetProjectTeammatesResponseTeammatesItem,
  GetProjectTeammatesResponseTeammatesItemRole,
  UpdateTeammateRequestRole,
} from '@/api/__generated__/types';
import { useCancelProjectInvitation } from '@/components/feature/project/hooks/useProjectInvitation';
import {
  ProjectOwnerCannotBeModifiedError,
  useRemoveProjectTeammate,
  useUpdateProjectTeammate,
} from '@/components/feature/project/hooks/useProjectTeammate';
import {
  AlertDialog,
  AlertDialogAction,
  AlertDialogCancel,
  AlertDialogContent,
  AlertDialogDescription,
  AlertDialogFooter,
  AlertDialogHeader,
  AlertDialogTitle,
  AlertDialogTrigger,
} from '@/components/ui/alert-dialog';
import { Avatar, AvatarFallback, AvatarImage } from '@/components/ui/avatar';
import { Badge } from '@/components/ui/badge';
import { Button } from '@/components/ui/button';
import {
  Select,
  SelectContent,
  SelectItem,
  SelectTrigger,
  SelectValue,
} from '@/components/ui/select';
import { Separator } from '@/components/ui/separator';
import {
  Table,
  TableBody,
  TableCell,
  TableHead,
  TableHeader,
  TableRow,
} from '@/components/ui/table';
import { useSession } from '@/lib/auth/client';

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
  const { data: session } = useSession();
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
              <TeammateRow
                key={teammate.user.handle}
                projectHandle={handle}
                teammate={teammate}
                canManage={
                  isAdmin &&
                  session != null &&
                  teammate.user.handle !== session.user.handle
                }
                roleLabel={ROLE_LABEL}
              />
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

function TeammateRow({
  projectHandle,
  teammate,
  canManage,
  roleLabel,
}: {
  projectHandle: string;
  teammate: GetProjectTeammatesResponseTeammatesItem;
  canManage: boolean;
  roleLabel: Record<GetProjectTeammatesResponseTeammatesItemRole, string>;
}) {
  const t = useTranslations('pages.projects.project.settings.teammates');
  const { mutate: updateTeammate, isPending: isUpdating } =
    useUpdateProjectTeammate();
  const { mutate: removeTeammate, isPending: isRemoving } =
    useRemoveProjectTeammate();
  const isPending = isUpdating || isRemoving;

  const handleError = (error: unknown, fallbackMessage: string) => {
    if (error instanceof ProjectOwnerCannotBeModifiedError) {
      toast.error(t('messages.owner-protected'));
      return;
    }

    toast.error(fallbackMessage);
  };

  const onRoleChange = (value: string) => {
    const role = value as UpdateTeammateRequestRole;
    if (role === teammate.role) {
      return;
    }

    updateTeammate(
      {
        handle: projectHandle,
        teammateHandle: teammate.user.handle,
        data: { role },
      },
      {
        onSuccess: () => toast.success(t('messages.update-success')),
        onError: (error) => handleError(error, t('messages.update-error')),
      },
    );
  };

  const onRemove = () => {
    removeTeammate(
      {
        handle: projectHandle,
        teammateHandle: teammate.user.handle,
      },
      {
        onSuccess: () => toast.success(t('messages.remove-success')),
        onError: (error) => handleError(error, t('messages.remove-error')),
      },
    );
  };

  return (
    <TableRow className="border-0">
      <TableCell className="border-l-0">
        <div className="flex items-center gap-3">
          <Avatar>
            <AvatarImage src={teammate.user.profileImageUrl ?? undefined} />
            <AvatarFallback>
              {teammate.user.name.charAt(0).toUpperCase()}
            </AvatarFallback>
          </Avatar>
          <div>
            <p className="text-sm font-medium">{teammate.user.name}</p>
            <p className="text-xs text-muted-foreground">
              @{teammate.user.handle}
            </p>
          </div>
        </div>
      </TableCell>
      <TableCell className="border-l-0">
        {canManage ? (
          <Select
            value={teammate.role}
            disabled={isPending}
            onValueChange={onRoleChange}
          >
            <SelectTrigger size="sm" aria-label={t('actions.edit-role')}>
              <SelectValue />
            </SelectTrigger>
            <SelectContent>
              {Object.values(UpdateTeammateRequestRole).map((role) => (
                <SelectItem key={role} value={role}>
                  {roleLabel[role]}
                </SelectItem>
              ))}
            </SelectContent>
          </Select>
        ) : (
          <Badge variant="outline">{roleLabel[teammate.role]}</Badge>
        )}
      </TableCell>
      <TableCell className="border-l-0">
        {canManage && (
          <div className="flex items-center justify-end">
            <AlertDialog>
              <AlertDialogTrigger asChild>
                <Button
                  variant="ghost"
                  size="icon"
                  aria-label={t('actions.remove')}
                  disabled={isPending}
                >
                  <TrashIcon className="h-4 w-4" />
                </Button>
              </AlertDialogTrigger>
              <AlertDialogContent>
                <AlertDialogHeader>
                  <AlertDialogTitle>
                    {t('remove-dialog.title')}
                  </AlertDialogTitle>
                  <AlertDialogDescription>
                    {t('remove-dialog.description', {
                      name: teammate.user.name,
                    })}
                  </AlertDialogDescription>
                </AlertDialogHeader>
                <AlertDialogFooter>
                  <AlertDialogCancel>
                    {t('remove-dialog.cancel')}
                  </AlertDialogCancel>
                  <AlertDialogAction
                    variant="destructive"
                    disabled={isRemoving}
                    onClick={onRemove}
                  >
                    {t('remove-dialog.confirm')}
                  </AlertDialogAction>
                </AlertDialogFooter>
              </AlertDialogContent>
            </AlertDialog>
          </div>
        )}
      </TableCell>
    </TableRow>
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
