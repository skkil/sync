import { useTranslations } from 'next-intl';

import { useGetProfileByHandle } from '@/api/__generated__/profile/profile';
import { Button } from '@/components/ui/button';
import { useFollowUserMutation } from '@/features/user/api/follow-user';
import { useUnfollowUserMutation } from '@/features/user/api/unfollow-user';
import { useSession } from '@/lib/auth/client';

interface FollowButtonProps {
  handle: string;
}

export default function FollowButton({ handle }: FollowButtonProps) {
  const t = useTranslations('pages.profile.header');

  const { data: session } = useSession();
  const { data: profile, isPending } = useGetProfileByHandle(handle);

  const { mutate: followUser } = useFollowUserMutation();
  const { mutate: unfollowUser } = useUnfollowUserMutation();

  if (isPending || !profile) {
    return null;
  }

  if (session?.user.id === profile.data.userId) {
    return null;
  }

  if (profile.data.isFollowing) {
    return (
      <Button
        onClick={() => {
          unfollowUser(handle);
        }}
      >
        {t('unfollow')}
      </Button>
    );
  } else {
    return (
      <Button
        onClick={() => {
          followUser(handle);
        }}
      >
        {t('follow')}
      </Button>
    );
  }
}
