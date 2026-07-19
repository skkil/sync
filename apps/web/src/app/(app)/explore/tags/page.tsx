import { Metadata } from 'next';
import { getTranslations } from 'next-intl/server';

import { requireOnboardedSession } from '@/lib/auth/guards';

import ExploreTags from './_components/ExploreTags';
import FollowingTags from './_components/FollowingTags';

export async function generateMetadata(): Promise<Metadata> {
  const t = await getTranslations('pages.explore.tags');

  return { title: t('title') };
}

export default async function ExploreTagsPage() {
  await requireOnboardedSession();

  const t = await getTranslations('pages.explore.tags');

  return (
    <div className="mx-auto space-y-8 px-4 py-8">
      <div className="space-y-2">
        <h1 className="text-2xl font-semibold">{t('title')}</h1>
        <p className="text-muted-foreground text-sm">{t('description')}</p>
      </div>

      <FollowingTags />

      <ExploreTags />
    </div>
  );
}
