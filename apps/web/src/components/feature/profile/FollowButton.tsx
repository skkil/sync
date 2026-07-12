'use client';

import { useTranslations } from 'next-intl';

import { useGetProfileByHandle } from '@/api/__generated__/profile/profile';
import {
  useFollowUser,
  useUnfollowUser,
} from '@/components/feature/user/hooks/useFollowUser';
import { Button } from '@/components/ui/button';
import { useRequireAuth } from '@/hooks/use-require-auth';
import { useSession } from '@/lib/auth/client';

interface FollowButtonProps {
  handle: string;
  size?: 'default' | 'sm';
}

export function FollowButton({ handle, size }: FollowButtonProps) {
  const t = useTranslations('pages.profile.header');

  const { data: session } = useSession();
  const { requireAuth } = useRequireAuth();
  const { data: profile, isPending } = useGetProfileByHandle(handle);

  const { mutate: followUser } = useFollowUser();
  const { mutate: unfollowUser } = useUnfollowUser();

  if (isPending || !profile) {
    return null;
  }

  if (String(session?.user.id) === String(profile.data.userId)) {
    return null;
  }

  const followeeId = String(profile.data.userId);

  if (profile.data.isFollowing) {
    return (
      <Button
        variant="outline"
        size={size}
        onClick={() => {
          if (!requireAuth({ intent: 'follow' })) {
            return;
          }

          unfollowUser({ followeeId });
        }}
      >
        {t('unfollow')}
      </Button>
    );
  }

  return (
    <Button
      size={size}
      onClick={() => {
        if (!requireAuth({ intent: 'follow' })) {
          return;
        }

        followUser({ followeeId });
      }}
    >
      {t('follow')}
    </Button>
  );
}
