'use client';

import {
  BellIcon,
  CheckIcon,
  UserPlusIcon,
  WarningCircleIcon,
} from '@phosphor-icons/react';
import { useInfiniteQuery, useQueryClient } from '@tanstack/react-query';
import Link from 'next/link';
import { useTranslations } from 'next-intl';
import { useState } from 'react';

import {
  getNotifications,
  useMarkAllNotificationsAsRead,
  useMarkNotificationAsRead,
} from '@/api/__generated__/notification/notification';
import type {
  GetNotificationsResponseNotificationsNodesItem,
  GetNotificationsResponseNotificationsNodesItemContent,
} from '@/api/__generated__/types';
import { Button } from '@/components/ui/button';
import {
  Empty,
  EmptyDescription,
  EmptyHeader,
  EmptyMedia,
  EmptyTitle,
} from '@/components/ui/empty';
import { Skeleton } from '@/components/ui/skeleton';
import { Tabs, TabsList, TabsTrigger } from '@/components/ui/tabs';
import { cn } from '@/lib/utils';

const NOTIFICATION_PAGE_SIZE = '20';

type NotificationFilter = 'ALL' | 'UNREAD';

export default function Notifications() {
  const t = useTranslations('pages.notifications');
  const queryClient = useQueryClient();
  const [filter, setFilter] = useState<NotificationFilter>('ALL');
  const status = filter === 'UNREAD' ? 'UNREAD' : undefined;

  const notificationsQuery = useInfiniteQuery({
    queryKey: ['/notifications', 'page', filter],
    initialPageParam: undefined as string | undefined,
    queryFn: ({ pageParam }) =>
      getNotifications({
        size: NOTIFICATION_PAGE_SIZE,
        cursor: pageParam,
        status,
      }),
    getNextPageParam: (lastPage) => {
      const pageInfo = lastPage.data.notifications?.pageInfo;
      return pageInfo?.hasNextPage ? pageInfo.endCursor || undefined : undefined;
    },
  });

  const notifications =
    notificationsQuery.data?.pages.flatMap(
      (page) => page.data.notifications?.nodes ?? [],
    ) ?? [];

  const { mutate: markNotificationAsRead } = useMarkNotificationAsRead({
    mutation: {
      onSuccess: () => {
        void queryClient.invalidateQueries({ queryKey: ['/notifications'] });
      },
    },
  });
  const { mutate: markAllNotificationsAsRead, isPending: isMarkAllPending } =
    useMarkAllNotificationsAsRead({
      mutation: {
        onSuccess: () => {
          void queryClient.invalidateQueries({ queryKey: ['/notifications'] });
        },
      },
    });

  return (
    <div className="mx-auto flex w-full max-w-3xl flex-col gap-4">
      <div className="flex flex-wrap items-center justify-between gap-3">
        <div className="space-y-1">
          <h1 className="text-2xl font-semibold">{t('title')}</h1>
          <p className="text-sm text-muted-foreground">{t('description')}</p>
        </div>

        <Button
          variant="outline"
          size="sm"
          isPending={isMarkAllPending}
          onClick={() => markAllNotificationsAsRead()}
        >
          <CheckIcon />
          {t('actions.mark-all-as-read')}
        </Button>
      </div>

      <Tabs
        value={filter}
        onValueChange={(value) => setFilter(value as NotificationFilter)}
      >
        <TabsList>
          <TabsTrigger value="ALL">{t('tabs.all')}</TabsTrigger>
          <TabsTrigger value="UNREAD">{t('tabs.unread')}</TabsTrigger>
        </TabsList>
      </Tabs>

      {notificationsQuery.isPending && <NotificationListSkeleton />}

      {notificationsQuery.isError && (
        <Empty className="min-h-80">
          <EmptyMedia variant="icon">
            <WarningCircleIcon />
          </EmptyMedia>
          <EmptyHeader>
            <EmptyTitle>{t('error.title')}</EmptyTitle>
            <EmptyDescription>{t('error.description')}</EmptyDescription>
          </EmptyHeader>
          <Button
            variant="outline"
            onClick={() => void notificationsQuery.refetch()}
          >
            {t('actions.retry')}
          </Button>
        </Empty>
      )}

      {notificationsQuery.isSuccess && notifications.length === 0 && (
        <Empty className="min-h-80">
          <EmptyMedia variant="icon">
            <BellIcon />
          </EmptyMedia>
          <EmptyHeader>
            <EmptyTitle>
              {filter === 'UNREAD'
                ? t('empty.UNREAD.title')
                : t('empty.ALL.title')}
            </EmptyTitle>
            <EmptyDescription>
              {filter === 'UNREAD'
                ? t('empty.UNREAD.description')
                : t('empty.ALL.description')}
            </EmptyDescription>
          </EmptyHeader>
        </Empty>
      )}

      {notifications.length > 0 && (
        <div className="divide-y rounded-md border">
          {notifications.map((node) => (
            <NotificationRow
              key={node.cursor}
              node={node}
              onRead={() =>
                markNotificationAsRead({
                  notificationId: String(node.content.id),
                })
              }
            />
          ))}
        </div>
      )}

      {notificationsQuery.hasNextPage && (
        <div className="flex justify-center py-2">
          <Button
            variant="outline"
            isPending={notificationsQuery.isFetchingNextPage}
            onClick={() => void notificationsQuery.fetchNextPage()}
          >
            {t('actions.load-more')}
          </Button>
        </div>
      )}
    </div>
  );
}

function NotificationRow({
  node,
  onRead,
}: {
  node: GetNotificationsResponseNotificationsNodesItem;
  onRead: () => void;
}) {
  const notification = node.content;
  const t = useTranslations('pages.notifications');
  const href = getNotificationHref(notification);

  const content = (
    <div
      className={cn(
        'flex w-full items-start gap-3 px-4 py-4 text-left transition-colors hover:bg-muted/50',
        notification.status === 'UNREAD' && 'bg-muted/30',
      )}
    >
      <NotificationIcon notification={notification} />

      <div className="min-w-0 flex-1 space-y-2">
        <div className="flex flex-wrap items-center gap-2">
          <p className="break-words text-sm leading-5">
            {getNotificationMessage(notification, t)}
          </p>
          {notification.status === 'UNREAD' && (
            <span className="size-2 rounded-full bg-primary" />
          )}
        </div>
        <p className="text-xs text-muted-foreground">
          {formatNotificationDate(notification.createdAt)}
        </p>
      </div>
    </div>
  );

  return (
    <div className="flex items-stretch">
      {href ? (
        <Link
          href={href}
          className="min-w-0 flex-1"
          onClick={() => {
            if (notification.status === 'UNREAD') {
              onRead();
            }
          }}
        >
          {content}
        </Link>
      ) : (
        <button
          type="button"
          className="min-w-0 flex-1"
          onClick={() => {
            if (notification.status === 'UNREAD') {
              onRead();
            }
          }}
        >
          {content}
        </button>
      )}

      {notification.status === 'UNREAD' && (
        <div className="flex items-center bg-muted/30 pr-4">
          <Button variant="ghost" size="sm" onClick={onRead}>
            <CheckIcon />
            {t('actions.mark-as-read')}
          </Button>
        </div>
      )}
    </div>
  );
}

function NotificationIcon({
  notification,
}: {
  notification: GetNotificationsResponseNotificationsNodesItemContent;
}) {
  const Icon = notification.type === 'USER_FOLLOWED' ? UserPlusIcon : BellIcon;

  return (
    <div
      className={cn(
        'mt-0.5 flex size-9 shrink-0 items-center justify-center rounded-full bg-muted text-muted-foreground',
        notification.status === 'UNREAD' && 'bg-primary/10 text-primary',
      )}
    >
      <Icon className="size-4" />
    </div>
  );
}

function NotificationListSkeleton() {
  return (
    <div className="space-y-2">
      {Array.from({ length: 5 }).map((_, index) => (
        <Skeleton key={index} className="h-20 w-full" />
      ))}
    </div>
  );
}

function getNotificationMessage(
  notification: GetNotificationsResponseNotificationsNodesItemContent,
  t: ReturnType<typeof useTranslations<'pages.notifications'>>,
) {
  if (notification.type === 'USER_FOLLOWED') {
    return t('items.USER_FOLLOWED', {
      name: getActorName(notification, t('unknown-actor')),
    });
  }

  if (notification.type === 'WELCOME') {
    return t('items.WELCOME');
  }

  return t('items.default');
}

function getActorName(
  notification: GetNotificationsResponseNotificationsNodesItemContent,
  fallback: string,
) {
  const actor = notification.actor;
  return actor?.name || actor?.handle || fallback;
}

function getNotificationHref(
  notification: GetNotificationsResponseNotificationsNodesItemContent,
) {
  if (notification.type === 'USER_FOLLOWED' && notification.actor?.handle) {
    return `/@${notification.actor.handle}`;
  }

  return undefined;
}

function formatNotificationDate(createdAt: string) {
  const date = new Date(createdAt);

  if (Number.isNaN(date.getTime())) {
    return createdAt;
  }

  return new Intl.DateTimeFormat('ko-KR', {
    dateStyle: 'medium',
    timeStyle: 'short',
  }).format(date);
}
