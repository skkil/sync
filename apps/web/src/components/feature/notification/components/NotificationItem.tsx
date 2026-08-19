'use client';

import { useTranslations } from 'next-intl';
import Link from 'next/link';

import type { GetNotificationsResponseNotificationsContentItem } from '@/api/__generated__/types/GetNotificationsResponseNotificationsContentItem';
import { ProfileAvatar } from '@/components/feature/profile/ProfileAvatar';
import { cn } from '@/lib/utils';
import ROUTES from '@/util/routes';

import NotificationActions from './NotificationActions';

interface NotificationItemProps {
  notification: GetNotificationsResponseNotificationsContentItem;
  onRead: (id: number) => void;
}

export default function NotificationItem({
  notification,
  onRead,
}: NotificationItemProps) {
  const t = useTranslations('components.navigation.notifications.types');
  const { payload, actor, status } = notification;

  const { href, message } = resolve(payload, t);

  const body = (
    <div className="flex items-start gap-3">
      {actor && (
        <ProfileAvatar
          name={actor.name ?? ''}
          seed={actor.handle ?? null}
          imageUrl={actor.profileImageUrl}
          size="sm"
          className="mt-0.5"
        />
      )}
      <p className="flex-1 text-sm">{message}</p>
      {status === 'UNREAD' && (
        <span className="mt-1.5 size-2 shrink-0 rounded-full bg-primary" />
      )}
    </div>
  );

  const triggerClassName = 'block w-full text-left';

  // 액션 버튼은 링크 안에 중첩할 수 없어(앵커 내부 인터랙티브 요소) 이동 영역과
  // 형제로 배치한다.
  return (
    <div
      className={cn(
        'rounded-md px-3 py-2 hover:bg-muted',
        status === 'UNREAD' && 'bg-muted/50',
      )}
    >
      {href ? (
        <Link
          href={href}
          className={triggerClassName}
          onClick={() => onRead(notification.id)}
        >
          {body}
        </Link>
      ) : (
        <button
          type="button"
          className={triggerClassName}
          onClick={() => onRead(notification.id)}
        >
          {body}
        </button>
      )}

      <NotificationActions
        notification={notification}
        onSettled={() => onRead(notification.id)}
      />
    </div>
  );
}

function resolve(
  payload: GetNotificationsResponseNotificationsContentItem['payload'],
  t: ReturnType<
    typeof useTranslations<'components.navigation.notifications.types'>
  >,
): { href: string | null; message: string } {
  switch (payload.type) {
    case 'NEW_COMMENT':
      return {
        href: ROUTES.POST(payload.postSlug),
        // SHORT 게시글은 제목이 없다 — 스키마상 필수지만 서버가 null을 보낼 수
        // 있는 기존 계약 결함이 있어, 렌더가 깨지지 않게 방어한다.
        message: payload.postTitle
          ? t('NEW_COMMENT', {
              name: payload.actorName,
              title: payload.postTitle,
            })
          : t('NEW_COMMENT_UNTITLED', { name: payload.actorName }),
      };
    case 'NEW_LIKE':
      return {
        href: ROUTES.POST(payload.postSlug),
        message: payload.postTitle
          ? t('NEW_LIKE', {
              name: payload.actorName,
              title: payload.postTitle,
            })
          : t('NEW_LIKE_UNTITLED', { name: payload.actorName }),
      };
    case 'NEW_FOLLOWER':
      return {
        href: ROUTES.PROFILE(payload.actorHandle),
        message: t('NEW_FOLLOWER', { name: payload.actorName }),
      };
    case 'PROJECT_INVITATION':
      return {
        href: ROUTES.PROJECT_INVITATIONS(),
        message: t('PROJECT_INVITATION', {
          name: payload.actorName,
          project: payload.projectName,
        }),
      };
    case 'PROJECT_INVITATION_ACCEPTED':
      return {
        href: ROUTES.PROJECT_SETTINGS_TEAMMATES(payload.projectHandle),
        message: t('PROJECT_INVITATION_ACCEPTED', {
          name: payload.actorName,
          project: payload.projectName,
        }),
      };
    case 'PROJECT_INVITATION_DECLINED':
      return {
        href: ROUTES.PROJECT_SETTINGS_TEAMMATES(payload.projectHandle),
        message: t('PROJECT_INVITATION_DECLINED', {
          name: payload.actorName,
          project: payload.projectName,
        }),
      };
    case 'PROJECT_JOIN_REQUEST':
      return {
        href: ROUTES.PROJECT_SETTINGS_TEAMMATES(payload.projectHandle),
        message: t('PROJECT_JOIN_REQUEST', {
          name: payload.actorName,
          project: payload.projectName,
        }),
      };
    case 'PROJECT_JOIN_REQUEST_APPROVED':
      return {
        href: ROUTES.PROJECT(payload.projectHandle),
        message: t('PROJECT_JOIN_REQUEST_APPROVED', {
          name: payload.actorName,
          project: payload.projectName,
        }),
      };
    case 'PROJECT_JOIN_REQUEST_DECLINED':
      return {
        href: ROUTES.PROJECT_JOIN_REQUESTS(),
        message: t('PROJECT_JOIN_REQUEST_DECLINED', {
          project: payload.projectName,
        }),
      };
    case 'NEW_MESSAGE':
      // 실시간 메시지 페이지는 아직 라우팅되지 않아(_legacy) 링크 없이 내용만 표시한다.
      return {
        href: null,
        message: t('NEW_MESSAGE', { name: payload.actorName }),
      };
    case 'WELCOME':
      return { href: null, message: t('WELCOME') };
    default:
      payload satisfies never;
      return { href: null, message: '' };
  }
}
