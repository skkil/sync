import { getTranslations } from 'next-intl/server';

import PublicPostFeed from '@/components/feature/post/viewer/PublicPostFeed';

export default async function ExplorePage() {
  const t = await getTranslations('pages.explore');

  return (
    <div className="mx-auto max-w-2xl space-y-6 px-4 py-8">
      <div className="space-y-2">
        <h1 className="text-2xl font-semibold">{t('title')}</h1>
        <p className="text-muted-foreground text-sm">{t('description')}</p>
      </div>

      <PublicPostFeed emptyMessage={t('empty')} />
    </div>
  );
}
