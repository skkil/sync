import { Metadata } from 'next';
import { getTranslations } from 'next-intl/server';

import { ProfileConnectionsList } from '@/components/feature/profile/ProfileConnectionsList';

interface ProfileFollowingProps {
  params: Promise<{ handle: string }>;
}

export async function generateMetadata(): Promise<Metadata> {
  const t = await getTranslations('pages.profile.connections.tabs');

  return { title: t('following') };
}

export default async function ProfileFollowing({
  params,
}: ProfileFollowingProps) {
  const { handle } = await params;

  return <ProfileConnectionsList handle={handle} type="following" />;
}
