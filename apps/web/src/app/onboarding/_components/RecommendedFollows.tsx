'use client';

import { useTranslations } from 'next-intl';
import { forwardRef, useEffect, useImperativeHandle, useState } from 'react';

import { useGetRecommendations } from '@/api/__generated__/user/user';
import {
  useFollowUser,
  useFollowedRecommendedUserIds,
  useUnfollowUser,
} from '@/components/feature/user/hooks/useFollowUser';
import { Avatar, AvatarFallback } from '@/components/ui/avatar';
import { Button } from '@/components/ui/button';
import { Skeleton } from '@/components/ui/skeleton';

import { OnboardingStepContentProps, OnboardingStepContentRef } from '../page';

const MAX_RECOMMENDED_FOLLOWS_COUNT = 5;

export const RecommendedFollows = forwardRef<
  OnboardingStepContentRef,
  OnboardingStepContentProps
>(({ onStateChange }, ref) => {
  const t = useTranslations('pages.onboarding.steps.follow');

  const { data, isPending } = useGetRecommendations();
  const followedIds = useFollowedRecommendedUserIds();
  const { mutate: followUser } = useFollowUser();
  const { mutate: unfollowUser } = useUnfollowUser();

  const [pendingIds, setPendingIds] = useState<Set<string>>(new Set());

  useImperativeHandle(ref, () => ({
    submit: (onSuccess) => onSuccess(),
  }));

  useEffect(() => {
    onStateChange({ isPending: false, isValid: true });
  }, [onStateChange]);

  const setPending = (userId: string, value: boolean) => {
    setPendingIds((prev) => {
      const next = new Set(prev);
      if (value) {
        next.add(userId);
      } else {
        next.delete(userId);
      }
      return next;
    });
  };

  const toggleFollow = (userId: string) => {
    setPending(userId, true);

    if (followedIds.includes(userId)) {
      unfollowUser(
        { followeeId: userId },
        { onSettled: () => setPending(userId, false) },
      );
      return;
    }

    followUser(
      { followeeId: userId },
      { onSettled: () => setPending(userId, false) },
    );
  };

  if (isPending) {
    return <RecommendedFollowsSkeleton />;
  }

  const users = (data?.data.users ?? []).slice(
    0,
    MAX_RECOMMENDED_FOLLOWS_COUNT,
  );

  if (users.length === 0) {
    return (
      <p className="text-muted-foreground text-sm py-6 text-center">
        {t('empty')}
      </p>
    );
  }

  return (
    <ul className="flex flex-col gap-1 -mx-2">
      {users.map((user, index) => {
        const isFollowing = followedIds.includes(user.userId);

        return (
          <li
            key={user.userId}
            style={{
              animationDelay: `${index * 60}ms`,
              animationFillMode: 'both',
            }}
            className="animate-in fade-in slide-in-from-bottom-1 duration-300 flex items-center gap-3 rounded-lg px-2 py-2 transition-colors hover:bg-muted/60"
          >
            <Avatar size="lg">
              <AvatarFallback className="bg-primary text-primary-foreground font-medium">
                {user.summary.name.charAt(0).toUpperCase()}
              </AvatarFallback>
            </Avatar>

            <div className="min-w-0 flex-1">
              <p className="truncate text-sm font-medium">
                {user.summary.name}
              </p>
              <p className="text-muted-foreground truncate text-xs">
                @{user.summary.handle}
              </p>
            </div>

            <Button
              size="sm"
              variant={isFollowing ? 'outline' : 'default'}
              isPending={pendingIds.has(user.userId)}
              onClick={() => toggleFollow(user.userId)}
            >
              {isFollowing ? t('following') : t('follow')}
            </Button>
          </li>
        );
      })}
    </ul>
  );
});
RecommendedFollows.displayName = 'RecommendedFollows';

function RecommendedFollowsSkeleton() {
  return (
    <ul className="flex flex-col gap-1 -mx-2">
      {Array.from({ length: 4 }).map((_, index) => (
        <li key={index} className="flex items-center gap-3 px-2 py-2">
          <Skeleton className="size-10 rounded-full" />
          <div className="flex min-w-0 flex-1 flex-col gap-1.5">
            <Skeleton className="h-4 w-24" />
            <Skeleton className="h-3 w-16" />
          </div>
          <Skeleton className="h-8 w-16 rounded-full" />
        </li>
      ))}
    </ul>
  );
}
