'use client';

import { useTranslations } from 'next-intl';
import Link from 'next/link';

import { useGetRecommendations } from '@/api/__generated__/user/user';
import {
  useFollowUser,
  useFollowedRecommendedUserIds,
} from '@/components/feature/user/hooks/useFollowUser';
import { Button } from '@/components/ui/button';
import { Skeleton } from '@/components/ui/skeleton';
import ROUTES from '@/util/routes';

const MAX_DISCOVER_USERS = 4;

export default function DiscoverCard() {
  const t = useTranslations('pages.home.discover');
  const followedUserIds = useFollowedRecommendedUserIds();
  const { data, isPending } = useGetRecommendations();
  const { mutate: followUser, isPending: isFollowPending } = useFollowUser();

  const users = (data?.data.users ?? []).slice(0, MAX_DISCOVER_USERS);

  return (
    <div className="space-y-4 rounded-xl border bg-card p-6">
      <div className="flex items-center justify-between">
        <span className="text-sm font-semibold">{t('title')}</span>
        <Link
          href={ROUTES.EXPLORE_TRENDING()}
          className="text-xs font-medium text-primary hover:underline"
        >
          {t('explore')} &rarr;
        </Link>
      </div>

      <div className="space-y-3">
        {isPending
          ? Array.from({ length: MAX_DISCOVER_USERS }).map((_, index) => (
              <div key={index} className="flex items-center gap-2">
                <Skeleton className="size-9 rounded-full" />
                <div className="flex-1 space-y-1">
                  <Skeleton className="h-3 w-20" />
                  <Skeleton className="h-3 w-14" />
                </div>
                <Skeleton className="h-7 w-16" />
              </div>
            ))
          : users.map((user) => {
              const isFollowing = followedUserIds.includes(user.userId);

              return (
                <div key={user.userId} className="flex items-center gap-2">
                  <div className="bg-primary text-primary-foreground flex size-9 shrink-0 items-center justify-center rounded-full text-sm font-semibold">
                    {user.summary.name.charAt(0).toUpperCase()}
                  </div>

                  <div className="min-w-0 flex-1">
                    <p className="truncate text-sm font-medium">
                      {user.summary.name}
                    </p>
                    {/* TODO: no bio/role field on user recommendations yet
                    — placeholder subtitle until that exists. */}
                    <p className="text-muted-foreground truncate text-xs">
                      @{user.summary.handle}
                    </p>
                  </div>

                  <Button
                    size="sm"
                    variant={isFollowing ? 'outline' : 'default'}
                    disabled={isFollowPending || isFollowing}
                    onClick={() => followUser({ followeeId: user.userId })}
                  >
                    {isFollowing ? t('following') : t('follow')}
                  </Button>
                </div>
              );
            })}
      </div>
    </div>
  );
}
