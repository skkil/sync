'use client';

import { BellIcon } from '@phosphor-icons/react';
import { useQueryClient } from '@tanstack/react-query';
import { useTranslations } from 'next-intl';
import { useState } from 'react';
import { toast } from 'sonner';

import {
  getGetNotificationsQueryKey,
  useMarkAllNotificationsAsRead,
  useMarkNotificationAsRead,
} from '@/api/__generated__/notification/notification';
import NotificationItem from '@/components/feature/notification/components/NotificationItem';
import { useNotifications } from '@/components/feature/notification/hooks/useNotifications';
import { Button } from '@/components/ui/button';
import {
  Empty,
  EmptyDescription,
  EmptyHeader,
  EmptyMedia,
  EmptyTitle,
} from '@/components/ui/empty';
import { Skeleton } from '@/components/ui/skeleton';
import SyncError, { ErrorCode } from '@/lib/error';

const PAGE_SIZE = '20';

export default function NotificationsList() {
  const t = useTranslations('pages.notifications');
  const tNotifications = useTranslations('components.navigation.notifications');
  const queryClient = useQueryClient();
  const [page, setPage] = useState(0);

  const params = { page: String(page), size: PAGE_SIZE };
  const { data, isPending } = useNotifications(params);

  const { mutate: markAsRead } = useMarkNotificationAsRead();
  const { mutate: markAllAsRead } = useMarkAllNotificationsAsRead();

  const notifications = data?.data.notifications?.content ?? [];
  const pageInfo = data?.data.notifications?.pageInfo;

  const invalidate = () =>
    queryClient.invalidateQueries({
      queryKey: getGetNotificationsQueryKey(params),
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

          toast.error(tNotifications('errors.mark-read'));
        },
      },
    );
  };

  const handleMarkAllAsRead = () => {
    markAllAsRead(undefined, { onSuccess: () => invalidate() });
  };

  return (
    <div className="flex w-full flex-col gap-5">
      <div className="flex items-center justify-between">
        <h1 className="text-2xl font-semibold">{t('title')}</h1>
        <Button variant="outline" size="sm" onClick={handleMarkAllAsRead}>
          {t('actions.mark-all-read')}
        </Button>
      </div>

      {isPending ? (
        <Skeleton className="h-72 w-full" />
      ) : notifications.length === 0 && page === 0 ? (
        <Empty className="min-h-80">
          <EmptyMedia variant="icon">
            <BellIcon />
          </EmptyMedia>
          <EmptyHeader>
            <EmptyTitle>{t('empty.title')}</EmptyTitle>
            <EmptyDescription>{t('empty.description')}</EmptyDescription>
          </EmptyHeader>
        </Empty>
      ) : (
        <div className="flex flex-col gap-1">
          {notifications.map((notification) => (
            <NotificationItem
              key={notification.id}
              notification={notification}
              onRead={handleRead}
            />
          ))}
        </div>
      )}

      {pageInfo && (
        <div className="flex items-center justify-end gap-2">
          <Button
            type="button"
            variant="outline"
            size="sm"
            disabled={!pageInfo.hasPreviousPage}
            onClick={() =>
              setPage((currentPage) => Math.max(0, currentPage - 1))
            }
          >
            {t('actions.previous')}
          </Button>
          <Button
            type="button"
            variant="outline"
            size="sm"
            disabled={!pageInfo.hasNextPage}
            onClick={() => setPage((currentPage) => currentPage + 1)}
          >
            {t('actions.next')}
          </Button>
        </div>
      )}
    </div>
  );
}
