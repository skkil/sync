'use client';

import { useQueryClient } from '@tanstack/react-query';
import { useTranslations } from 'next-intl';
import { notFound } from 'next/navigation';
import { useEffect } from 'react';

import { getGetTagQueryKey, useGetTag } from '@/api/__generated__/tag/tag';
import { TagBadge } from '@/components/feature/tag/TagBadge';
import { useFollowTag } from '@/components/feature/tag/hooks/useFollowTag';
import { useUnfollowTag } from '@/components/feature/tag/hooks/useUnfollowTag';
import { Button } from '@/components/ui/button';
import { Card, CardContent } from '@/components/ui/card';
import { Skeleton } from '@/components/ui/skeleton';
import { useRequireAuth } from '@/hooks/use-require-auth';
import { useSession } from '@/lib/auth/client';
import SyncError, { ErrorCode } from '@/lib/error';

interface TagDetailHeaderProps {
  tagId: string;
}

export default function TagDetailHeader({ tagId }: TagDetailHeaderProps) {
  const t = useTranslations('components.tag.detail');
  const { data: session } = useSession();
  const handle = session?.user.handle ?? '';
  const { requireAuth } = useRequireAuth();
  const queryClient = useQueryClient();

  const { data, isPending, error, isError } = useGetTag(tagId);

  useEffect(() => {
    if (isError && error instanceof SyncError) {
      switch (error.code) {
        case ErrorCode.TAG_NOT_FOUND:
          notFound();
      }
    }
  }, [error, isError]);

  const invalidateTag = () =>
    queryClient.invalidateQueries({ queryKey: getGetTagQueryKey(tagId) });

  const { mutate: followTag, isPending: isFollowPending } = useFollowTag({
    handle,
    onSettled: invalidateTag,
  });
  const { mutate: unfollowTag, isPending: isUnfollowPending } = useUnfollowTag({
    handle,
    onSettled: invalidateTag,
  });

  if (isPending || !data) {
    return <TagDetailHeaderSkeleton />;
  }

  const tag = data.data.tag;
  if (!tag) {
    return <TagDetailHeaderSkeleton />;
  }

  const isProjectTag = !!tag.projectHandle;

  const handleFollowToggle = () => {
    if (!requireAuth({ intent: 'follow' })) {
      return;
    }

    if (tag.isFollowing) {
      unfollowTag({ tagId });
      return;
    }

    followTag({ tagId });
  };

  return (
    <Card>
      <CardContent className="flex flex-col gap-4 sm:flex-row sm:items-start sm:justify-between">
        <div className="space-y-1">
          <TagBadge
            name={tag.name}
            isProjectTag={isProjectTag}
            variant="secondary"
            className="w-fit text-base"
          />

          <p className="text-muted-foreground text-sm">
            {tag.description || t('description-empty')}
          </p>

          <p className="text-muted-foreground text-xs">
            {t('meta', {
              postCount: tag.postCount,
              followerCount: tag.followerCount,
            })}
          </p>
        </div>

        {!isProjectTag && (
          <Button
            variant={tag.isFollowing ? 'outline' : 'default'}
            disabled={isFollowPending || isUnfollowPending}
            onClick={handleFollowToggle}
            className="shrink-0"
          >
            {tag.isFollowing ? t('follow.following') : t('follow.follow')}
          </Button>
        )}
      </CardContent>
    </Card>
  );
}

function TagDetailHeaderSkeleton() {
  return (
    <Card>
      <CardContent className="space-y-2">
        <Skeleton className="h-6 w-24" />
        <Skeleton className="h-4 w-64" />
        <Skeleton className="h-3 w-32" />
      </CardContent>
    </Card>
  );
}
