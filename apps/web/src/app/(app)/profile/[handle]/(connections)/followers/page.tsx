import { Metadata } from 'next';
import { getTranslations } from 'next-intl/server';

import { ProfileConnectionsList } from '@/components/feature/profile/ProfileConnectionsList';

interface ProfileFollowersProps {
  params: Promise<{ handle: string }>;
}

export async function generateMetadata(): Promise<Metadata> {
  const t = await getTranslations('pages.profile.connections.tabs');

  return { title: t('followers') };
}

export default async function ProfileFollowers({
  params,
}: ProfileFollowersProps) {
  const { handle } = await params;

  return <ProfileConnectionsList handle={handle} type="followers" />;
}
