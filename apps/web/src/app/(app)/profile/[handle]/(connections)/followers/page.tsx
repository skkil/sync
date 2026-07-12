import { ProfileConnectionsList } from '@/components/feature/profile/ProfileConnectionsList';

interface ProfileFollowersProps {
  params: Promise<{ handle: string }>;
}

export default async function ProfileFollowers({
  params,
}: ProfileFollowersProps) {
  const { handle } = await params;

  return <ProfileConnectionsList handle={handle} type="followers" />;
}
