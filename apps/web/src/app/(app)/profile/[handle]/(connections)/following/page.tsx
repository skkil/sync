import { ProfileConnectionsList } from '@/components/feature/profile/ProfileConnectionsList';

interface ProfileFollowingProps {
  params: Promise<{ handle: string }>;
}

export default async function ProfileFollowing({
  params,
}: ProfileFollowingProps) {
  const { handle } = await params;

  return <ProfileConnectionsList handle={handle} type="following" />;
}
