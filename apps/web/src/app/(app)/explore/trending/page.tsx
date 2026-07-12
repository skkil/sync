import { getTranslations } from 'next-intl/server';

import { PostRecommendationType } from '@/components/feature/post/types/post';
import { requireOnboardedSession } from '@/lib/auth/guards';

import ExplorePosts from '../_components/ExplorePosts';

export default async function ExploreTrendingPage() {
  await requireOnboardedSession();

  const t = await getTranslations('pages.explore');

  return (
    <div className="mx-auto space-y-6 px-4 py-8">
      <div className="space-y-2">
        <h1 className="text-2xl font-semibold">{t('title')}</h1>
        <p className="text-muted-foreground text-sm">{t('description')}</p>
      </div>

      <ExplorePosts type={PostRecommendationType.TRENDING} />
    </div>
  );
}
