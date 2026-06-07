'use client';

import {
  BellIcon,
  UserPlusIcon,
  WarningCircleIcon,
} from '@phosphor-icons/react';
import { useQueryClient } from '@tanstack/react-query';
import { useTranslations } from 'next-intl';
import Link from 'next/link';
import { useState } from 'react';

import {
  useGetNotifications,
  useMarkNotificationAsRead,
} from '@/api/__generated__/notification/notification';
import type { GetNotificationsResponseNotificationsNodesItemContent } from '@/api/__generated__/types';
import { Button } from '@/components/ui/button';
import {
  DropdownMenu,
  DropdownMenuContent,
  DropdownMenuItem,
  DropdownMenuLabel,
  DropdownMenuSeparator,
  DropdownMenuTrigger,
} from '@/components/ui/dropdown-menu';
import { Spinner } from '@/components/ui/spinner';
import { useSession } from '@/lib/auth/client';
import { cn } from '@/lib/utils';

interface NotificationDropdownProps {
  align?: 'start' | 'end';
}

export default function NotificationDropdown({
  align = 'end',
}: NotificationDropdownProps) {
  const t = useTranslations('components.navigation.notifications');
  const queryClient = useQueryClient();
  const { data: session, isPending: isSessionPending } = useSession();
  const [open, setOpen] = useState(false);

  const notificationsQuery = useGetNotifications(
    { size: '10', status: 'UNREAD' },
    {
      query: {
        enabled: !!session?.user,
        staleTime: 30_000,
      },
    },
  );
  const { mutate: markNotificationAsRead } = useMarkNotificationAsRead({
    mutation: {
      onSuccess: () => {
        void queryClient.invalidateQueries({ queryKey: ['/notifications'] });
      },
    },
  });

  if (isSessionPending || !session?.user) {
    return null;
  }

  const notifications =
    notificationsQuery.data?.data.notifications?.nodes.map((node) => ({
      cursor: node.cursor,
      notification: node.content,
    })) ?? [];

  const hasUnreadNotification = notifications.some(
    ({ notification }) => notification.status === 'UNREAD',
  );

  return (
    <DropdownMenu modal={false} open={open} onOpenChange={setOpen}>
      <DropdownMenuTrigger asChild>
        <Button
          variant="ghost"
          size="icon"
          className="relative"
          aria-label={t('label')}
        >
          <BellIcon />
          {hasUnreadNotification && (
            <span className="absolute right-2 top-2 size-2 rounded-full bg-primary" />
          )}
        </Button>
      </DropdownMenuTrigger>

      <DropdownMenuContent
        align={align}
        className="w-80 max-w-[calc(100vw-2rem)] p-0"
      >
        <DropdownMenuLabel className="px-4 py-3">
          <Link
            href="/notifications"
            className="text-sm font-semibold text-foreground hover:underline"
            onClick={() => setOpen(false)}
          >
            {t('title')}
          </Link>
        </DropdownMenuLabel>
        <DropdownMenuSeparator />

        {notificationsQuery.isPending && (
          <div className="flex h-24 items-center justify-center">
            <Spinner />
          </div>
        )}

        {notificationsQuery.isError && (
          <div className="flex flex-col items-center gap-3 px-4 py-6 text-center">
            <WarningCircleIcon className="size-5 text-muted-foreground" />
            <p className="text-sm text-muted-foreground">{t('error')}</p>
            <Button
              variant="outline"
              size="sm"
              onClick={() => void notificationsQuery.refetch()}
            >
              {t('retry')}
            </Button>
          </div>
        )}

        {notificationsQuery.isSuccess && notifications.length === 0 && (
          <div className="px-4 py-8 text-center text-sm text-muted-foreground">
            {t('empty')}
          </div>
        )}

        {notificationsQuery.isSuccess && notifications.length > 0 && (
          <div className="max-h-96 overflow-y-auto p-1">
            {notifications.map(({ cursor, notification }) => (
              <NotificationMenuItem
                key={cursor}
                notification={notification}
                onSelect={() => {
                  if (notification.status === 'UNREAD') {
                    markNotificationAsRead({
                      notificationId: String(notification.id),
                    });
                  }
                  setOpen(false);
                }}
              />
            ))}
          </div>
        )}

        <DropdownMenuSeparator />
        <div className="p-1">
          <DropdownMenuItem asChild>
            <Link href="/notifications" onClick={() => setOpen(false)}>
              {t('view-all')}
            </Link>
          </DropdownMenuItem>
        </div>
      </DropdownMenuContent>
    </DropdownMenu>
  );
}

function NotificationMenuItem({
  notification,
  onSelect,
}: {
  notification: GetNotificationsResponseNotificationsNodesItemContent;
  onSelect: () => void;
}) {
  const t = useTranslations('components.navigation.notifications');
  const actor = notification.actor ?? undefined;
  const actorName = actor?.name || actor?.handle || t('unknown-actor');
  const href = actor?.handle ? `/@${actor.handle}` : undefined;
  const NotificationIcon =
    notification.type === 'USER_FOLLOWED' ? UserPlusIcon : BellIcon;

  const content = (
    <div className="flex w-full items-start gap-3 px-3 py-3">
      <div
        className={cn(
          'mt-0.5 flex size-8 shrink-0 items-center justify-center rounded-full bg-muted text-muted-foreground',
          notification.status === 'UNREAD' && 'bg-primary/10 text-primary',
        )}
      >
        <NotificationIcon className="size-4" />
      </div>

      <div className="min-w-0 flex-1 space-y-1">
        <p className="whitespace-normal break-words text-sm leading-5">
          {getNotificationMessage(notification.type, actorName, t)}
        </p>
        <p className="text-xs text-muted-foreground">
          {formatNotificationDate(notification.createdAt)}
        </p>
      </div>

      {notification.status === 'UNREAD' && (
        <span className="mt-2 size-2 shrink-0 rounded-full bg-primary" />
      )}
    </div>
  );

  if (!href) {
    return (
      <DropdownMenuItem className="items-start p-0" onSelect={onSelect}>
        {content}
      </DropdownMenuItem>
    );
  }

  return (
    <DropdownMenuItem asChild className="items-start p-0">
      <Link href={href} onClick={onSelect}>
        {content}
      </Link>
    </DropdownMenuItem>
  );
}

function getNotificationMessage(
  type: string,
  actorName: string,
  t: ReturnType<typeof useTranslations<'components.navigation.notifications'>>,
) {
  if (type === 'USER_FOLLOWED') {
    return t('items.USER_FOLLOWED', { name: actorName });
  }

  if (type === 'WELCOME') {
    return t('items.WELCOME');
  }

  return t('items.default');
}

function formatNotificationDate(createdAt: string) {
  const date = new Date(createdAt);

  if (Number.isNaN(date.getTime())) {
    return createdAt;
  }

  return new Intl.DateTimeFormat('ko-KR', {
    month: 'short',
    day: 'numeric',
    hour: '2-digit',
    minute: '2-digit',
  }).format(date);
}
