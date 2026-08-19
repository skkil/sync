'use client';

import { BellIcon } from '@phosphor-icons/react';
import { useQueryClient } from '@tanstack/react-query';
import { useTranslations } from 'next-intl';
import Link from 'next/link';
import { toast } from 'sonner';

import {
  getGetNotificationsQueryKey,
  useMarkAllNotificationsAsRead,
  useMarkNotificationAsRead,
} from '@/api/__generated__/notification/notification';
import NotificationItem from '@/components/feature/notification/components/NotificationItem';
import { useNotifications } from '@/components/feature/notification/hooks/useNotifications';
import { Badge } from '@/components/ui/badge';
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
import { useMounted } from '@/hooks/use-mounted';
import { useSession } from '@/lib/auth/client';
import SyncError, { ErrorCode } from '@/lib/error';
import ROUTES from '@/util/routes';

const PREVIEW_PARAMS = { page: '0', size: '5' };

export default function NotificationsButton() {
  const t = useTranslations('components.navigation.notifications');
  const queryClient = useQueryClient();
  const mounted = useMounted();

  const { isPending: isSessionPending } = useSession();
  const { data } = useNotifications(PREVIEW_PARAMS);

  const { mutate: markAsRead } = useMarkNotificationAsRead();
  const { mutate: markAllAsRead } = useMarkAllNotificationsAsRead();

  const notifications = data?.data.notifications?.content ?? [];
  const unreadCount = data?.data.unreadCount ?? 0;
  const unreadLabel = unreadCount > 99 ? '99+' : String(unreadCount);

  const invalidate = () =>
    queryClient.invalidateQueries({
      queryKey: getGetNotificationsQueryKey(PREVIEW_PARAMS),
    });

  const handleRead = (id: number) => {
    markAsRead(
      { notificationId: String(id) },
      {
        onSuccess: () => invalidate(),
        onError: (error) => {
          // 이미 사라진 알림이면 오류가 아니라 목록이 낡은 것이다 — 조용히 맞춘다.
          if (
            error instanceof SyncError &&
            error.code === ErrorCode.NOTIFICATION_NOT_FOUND
          ) {
            invalidate();
            return;
          }

          toast.error(t('errors.mark-read'));
        },
      },
    );
  };

  const handleMarkAllAsRead = () => {
    markAllAsRead(undefined, { onSuccess: () => invalidate() });
  };

  return (
    <DropdownMenu>
      <DropdownMenuTrigger asChild>
        <Button
          variant="ghost"
          size="icon"
          className="relative"
          aria-label={t('aria-label')}
        >
          <BellIcon size={20} />
          {mounted && !isSessionPending && unreadCount > 0 && (
            <Badge className="absolute -right-1 -top-1 size-4 justify-center rounded-full p-0 text-[10px]">
              {unreadLabel}
            </Badge>
          )}
        </Button>
      </DropdownMenuTrigger>
      <DropdownMenuContent align="end" className="w-80">
        <div className="flex items-center justify-between px-2 py-1.5">
          <DropdownMenuLabel className="p-0">{t('title')}</DropdownMenuLabel>
          {unreadCount > 0 && (
            <Button
              variant="ghost"
              size="sm"
              className="h-auto p-0 text-xs"
              onClick={handleMarkAllAsRead}
            >
              {t('mark-all-read')}
            </Button>
          )}
        </div>
        <DropdownMenuSeparator />

        {!mounted || isSessionPending ? (
          <div className="flex justify-center py-6">
            <Spinner />
          </div>
        ) : notifications.length === 0 ? (
          <p className="px-3 py-6 text-center text-sm text-muted-foreground">
            {t('empty')}
          </p>
        ) : (
          <div className="flex max-h-96 flex-col gap-1 overflow-y-auto">
            {notifications.map((notification) => (
              <NotificationItem
                key={notification.id}
                notification={notification}
                onRead={handleRead}
              />
            ))}
          </div>
        )}

        <DropdownMenuSeparator />
        <DropdownMenuItem asChild>
          <Link
            href={ROUTES.NOTIFICATIONS()}
            className="justify-center text-sm"
          >
            {t('view-all')}
          </Link>
        </DropdownMenuItem>
      </DropdownMenuContent>
    </DropdownMenu>
  );
}
