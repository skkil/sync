'use client';

import { TrendUpIcon } from '@phosphor-icons/react/dist/ssr';
import { useTranslations } from 'next-intl';
import Link from 'next/link';
import { useState } from 'react';

import { useGetTagRecommendations } from '@/api/__generated__/tag/tag';
import { useFollowTag } from '@/components/feature/tag/hooks/useFollowTag';
import { useUnfollowTag } from '@/components/feature/tag/hooks/useUnfollowTag';
import { Button } from '@/components/ui/button';
import { Skeleton } from '@/components/ui/skeleton';
import { useSession } from '@/lib/auth/client';
import ROUTES from '@/util/routes';

const MAX_TRENDING_TAGS = 5;

export default function TrendingTags() {
  const t = useTranslations('pages.home.trending-tags');

  const { data: session } = useSession();
  const handle = session?.user.handle ?? '';

  const { data, isPending } = useGetTagRecommendations();
  const tags = (data?.data.tags ?? []).slice(0, MAX_TRENDING_TAGS);

  const [followingOverrides, setFollowingOverrides] = useState<
    Record<number, boolean>
  >({});
  const [pendingIds, setPendingIds] = useState<Set<number>>(new Set());
  const setPending = (tagId: number, value: boolean) => {
    setPendingIds((prev) => {
      const next = new Set(prev);
      if (value) {
        next.add(tagId);
      } else {
        next.delete(tagId);
      }
      return next;
    });
  };

  const { mutate: followTag } = useFollowTag({ handle });
  const { mutate: unfollowTag } = useUnfollowTag({ handle });

  const toggleFollow = (tagId: number, isFollowing: boolean) => {
    setPending(tagId, true);
    setFollowingOverrides((prev) => ({ ...prev, [tagId]: !isFollowing }));

    const onSettled = () => setPending(tagId, false);

    if (isFollowing) {
      unfollowTag({ tagId: String(tagId) }, { onSettled });
    } else {
      followTag({ tagId: String(tagId) }, { onSettled });
    }
  };

  return (
    <div className="space-y-4 rounded-xl border bg-card p-6">
      <div className="flex items-center justify-between">
        <span className="flex items-center gap-1.5 text-sm font-semibold">
          <TrendUpIcon />
          {t('title')}
        </span>
        <Link
          href={ROUTES.EXPLORE_TAGS()}
          className="text-xs font-medium text-primary hover:underline"
        >
          {t('explore')} &rarr;
        </Link>
      </div>

      <div className="space-y-3">
        {isPending
          ? Array.from({ length: MAX_TRENDING_TAGS }).map((_, index) => (
              <div key={index} className="flex items-center gap-2">
                <div className="flex-1 space-y-1">
                  <Skeleton className="h-3 w-20" />
                  <Skeleton className="h-3 w-14" />
                </div>
                <Skeleton className="h-7 w-16" />
              </div>
            ))
          : tags.map((tag) => {
              const isFollowing = followingOverrides[tag.id] ?? tag.isFollowing;

              return (
                <div key={tag.id} className="flex items-center gap-2">
                  <Link
                    href={
                      tag.projectHandle
                        ? ROUTES.PROJECT_TAG(tag.projectHandle, String(tag.id))
                        : ROUTES.TAG(String(tag.id))
                    }
                    className="min-w-0 flex-1 rounded-md hover:underline"
                  >
                    <p className="truncate text-sm font-semibold">
                      #{tag.name}
                    </p>
                    <p className="text-muted-foreground truncate text-xs">
                      {t('post-count', { count: tag.postCount })}
                    </p>
                  </Link>

                  <Button
                    size="sm"
                    variant={isFollowing ? 'outline' : 'default'}
                    isPending={pendingIds.has(tag.id)}
                    onClick={() => toggleFollow(tag.id, isFollowing)}
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
