import { useTranslations } from 'next-intl';

import { Button } from '@/components/ui/button';
import { useGetProfileQuery } from '@/features/profile/api/get-profile';
import { useFollowUserMutation } from '@/features/user/api/follow-user';
import { useUnfollowUserMutation } from '@/features/user/api/unfollow-user';
import { useSession } from '@/lib/auth/client';

interface FollowButtonProps {
  userId: string;
}

export default function FollowButton({ userId }: FollowButtonProps) {
  const t = useTranslations('pages.profile.header');

  const { data: session } = useSession();
  const { data: profile, isPending } = useGetProfileQuery(userId);

  const { mutate: followUser } = useFollowUserMutation();
  const { mutate: unfollowUser } = useUnfollowUserMutation();

  if (isPending || !profile) {
    return null;
  }

  if (session?.user.id === userId) {
    return null;
  }

  if (profile.isFollowing) {
    return (
      <Button
        onClick={() => {
          unfollowUser(userId);
        }}
      >
        {t('unfollow')}
      </Button>
    );
  } else {
    return (
      <Button
        onClick={() => {
          followUser(userId);
        }}
      >
        {t('follow')}
      </Button>
    );
  }
}
