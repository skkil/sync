'use client';

import { useTranslations } from 'next-intl';
import { useState } from 'react';

import { useGetUserRecommendations } from '@/api/__generated__/user/user';
import { ProfileHoverCard } from '@/components/feature/profile/ProfileHoverCard';
import {
  useFollowUser,
  useFollowedRecommendedUserIds,
} from '@/components/feature/user/hooks/useFollowUser';
import { Button } from '@/components/ui/button';
import { Skeleton } from '@/components/ui/skeleton';

const MAX_DISCOVER_USERS = 4;

export default function DiscoverCard() {
  const t = useTranslations('pages.home.discover');
  const followedUserIds = useFollowedRecommendedUserIds();
  const { data, isPending, isError, isFetching, refetch } =
    useGetUserRecommendations();
  const { mutate: followUser } = useFollowUser();
  const [pendingUserIds, setPendingUserIds] = useState<Set<string>>(new Set());

  const users = (data?.data.users ?? []).slice(0, MAX_DISCOVER_USERS);
  const hasInitialError = isError && !data;

  const follow = (userId: string) => {
    setPendingUserIds((previous) => new Set(previous).add(userId));
    followUser(
      { followeeId: userId },
      {
        onSettled: () => {
          setPendingUserIds((previous) => {
            const next = new Set(previous);
            next.delete(userId);
            return next;
          });
        },
      },
    );
  };

  return (
    <div className="space-y-4 rounded-xl border bg-card p-6">
      <div className="flex items-center justify-between">
        <span className="text-sm font-semibold">{t('title')}</span>
      </div>

      <div className="space-y-3">
        {isPending ? (
          Array.from({ length: MAX_DISCOVER_USERS }).map((_, index) => (
            <div key={index} className="flex items-center gap-2">
              <Skeleton className="size-9 rounded-full" />
              <div className="flex-1 space-y-1">
                <Skeleton className="h-3 w-20" />
                <Skeleton className="h-3 w-14" />
              </div>
              <Skeleton className="h-7 w-16" />
            </div>
          ))
        ) : hasInitialError ? (
          <div role="alert" className="flex flex-col items-start gap-2 py-2">
            <p className="text-muted-foreground text-sm">
              {t('error.description')}
            </p>
            <Button
              size="sm"
              variant="outline"
              disabled={isFetching}
              onClick={() => void refetch()}
            >
              {t('error.retry')}
            </Button>
          </div>
        ) : users.length === 0 ? (
          <p className="text-muted-foreground py-2 text-sm">{t('empty')}</p>
        ) : (
          users.map((user) => {
            const isFollowing = followedUserIds.includes(user.userId);

            return (
              <div key={user.userId} className="flex items-center gap-2">
                <ProfileHoverCard
                  handle={user.summary.handle}
                  name={user.summary.name}
                  imageUrl={user.summary.profileImageUrl ?? undefined}
                />

                <div className="min-w-0 flex-1">
                  <p className="truncate text-sm font-medium">
                    {user.summary.name}
                  </p>
                  {/* TODO: 사용자 추천 응답에 소개/역할 필드가 추가되기 전까지
                    핸들을 보조 문구로 사용한다. */}
                  <p className="text-muted-foreground truncate text-xs">
                    @{user.summary.handle}
                  </p>
                </div>

                <Button
                  size="sm"
                  variant={isFollowing ? 'outline' : 'default'}
                  isPending={pendingUserIds.has(user.userId)}
                  disabled={isFollowing}
                  onClick={() => follow(user.userId)}
                >
                  {isFollowing ? t('following') : t('follow')}
                </Button>
              </div>
            );
          })
        )}
      </div>
    </div>
  );
}
