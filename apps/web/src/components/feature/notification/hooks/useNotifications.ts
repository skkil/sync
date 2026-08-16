'use client';

import { useQueryClient } from '@tanstack/react-query';
import { useEffect, useRef } from 'react';

import {
  getGetNotificationsQueryKey,
  useGetNotifications,
} from '@/api/__generated__/notification/notification';
import type { GetNotificationsParams } from '@/api/__generated__/types/GetNotificationsParams';
import type { GetNotificationsResponse } from '@/api/__generated__/types/GetNotificationsResponse';
import type { GetNotificationsResponseNotificationsContentItem } from '@/api/__generated__/types/GetNotificationsResponseNotificationsContentItem';
import { useWebSocket } from '@/components/providers/WebSocketProvider';
import { useSession } from '@/lib/auth/client';

const WEBSOCKET_ENABLED = process.env.NEXT_PUBLIC_WEBSOCKET_ENABLED === 'true';

/**
 * 사용자 목적지 — 브로커가 세션별로 해소하므로 목적지에 사용자 ID를 넣지
 * 않는다. ID가 든 공개 토픽은 남의 알림을 구독할 수 있어 서버에서 거부된다.
 */
const NOTIFICATION_DESTINATION = '/user/queue/notifications';

/**
 * 알림 목록 조회와 WebSocket 실시간 구독을 함께 처리하는 공용 훅.
 * 드롭다운 미리보기와 전체 알림 페이지가 동일한 구독 로직을 공유한다.
 *
 * 구독은 연결 성립 전에 걸어도 되며(예약 후 연결 직후 성립), 재연결마다
 * 자동으로 복원된다. 같은 목적지에 두 소비자가 붙어도 실제 STOMP 구독은
 * 하나다. WebSocket이 꺼져 있으면 구독을 건너뛰고, 이때 실시간 갱신은
 * 없으며 쿼리가 다시 실행되는 시점(마운트·무효화)에만 목록을 가져온다.
 */
export function useNotifications(params: GetNotificationsParams) {
  const { page, size } = params;
  const { data: session } = useSession();
  const { subscribe } = useWebSocket();
  const queryClient = useQueryClient();

  const query = useGetNotifications(params, {
    query: { enabled: !!session?.user.id },
  });

  // 페이지 이동으로 구독을 끊었다 다시 걸지 않도록, 쿼리 키 파라미터는
  // ref로만 읽는다.
  const paramsRef = useRef<GetNotificationsParams>({ page, size });
  useEffect(() => {
    paramsRef.current = { page, size };
  }, [page, size]);

  useEffect(() => {
    if (!WEBSOCKET_ENABLED || !session?.user.id) {
      return;
    }

    // 최초 구독 직후에는 방금 마운트 쿼리가 돌았으므로 다시 받아올 필요가 없다.
    let isFirstSubscription = true;

    const subscription = subscribe(
      NOTIFICATION_DESTINATION,
      (message) => {
        const notification = JSON.parse(
          message.body,
        ) as GetNotificationsResponseNotificationsContentItem;
        const currentParams = paramsRef.current;

        // 첫 페이지가 아닌 캐시에 prepend하면 그 페이지 내용이 틀려지고(새
        // 알림은 0페이지 소속) 이후 0페이지 조회와 중복된다. 보고 있는 키를
        // 서버 상태로 다시 맞추는 것으로 대신한다.
        if (currentParams.page && currentParams.page !== '0') {
          void queryClient.invalidateQueries({
            queryKey: getGetNotificationsQueryKey(currentParams),
          });
          return;
        }

        queryClient.setQueryData<{ data: GetNotificationsResponse }>(
          getGetNotificationsQueryKey(currentParams),
          (old) => {
            if (!old) {
              return old;
            }

            const previous = old.data.notifications;

            // 같은 알림이 이미 반영돼 있으면 아무것도 하지 않는다. 두 소비자가
            // 같은 쿼리 키를 쓰게 되면 리스너가 각각 돌아 unreadCount가 두 번
            // 오르는데, ID 멱등성이 그 경우를 막는다.
            if (
              previous?.content?.some((item) => item.id === notification.id)
            ) {
              return old;
            }

            return {
              ...old,
              data: {
                ...old.data,
                unreadCount: old.data.unreadCount + 1,
                notifications: previous
                  ? {
                      ...previous,
                      content: [notification, ...(previous.content ?? [])],
                    }
                  : previous,
              },
            };
          },
        );
      },
      {
        // 최초 구독과 매 재연결에서 호출된다. 끊겨 있던 동안 도착한 알림은
        // 프레임으로 못 받았으므로 서버 상태로 다시 맞춘다.
        onSubscribed: () => {
          if (isFirstSubscription) {
            isFirstSubscription = false;
            return;
          }

          void queryClient.invalidateQueries({
            queryKey: getGetNotificationsQueryKey(paramsRef.current),
          });
        },
      },
    );

    return () => {
      subscription?.unsubscribe();
    };
  }, [session?.user.id, queryClient, subscribe]);

  return query;
}
