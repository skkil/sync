import { Metadata } from 'next';
import { getTranslations } from 'next-intl/server';

import { TwoColumnLayout } from '@/components/layout/TwoColumnLayout';
import { requireOnboardedSession } from '@/lib/auth/guards';

import DiscoverCard from './_components/DiscoverCard';
import HomeFeed from './_components/HomeFeed';
import TrendingTags from './_components/TrendingTags';

export async function generateMetadata(): Promise<Metadata> {
  const t = await getTranslations('pages.home.feed');

  return { title: t('title') };
}

export default async function Home() {
  await requireOnboardedSession();
  const t = await getTranslations('pages.home.feed');

  return (
    <div className="space-y-6">
      <div className="space-y-1">
        <h1 className="text-2xl font-semibold">{t('title')}</h1>
        <p className="text-muted-foreground text-sm">{t('description')}</p>
      </div>

      <TwoColumnLayout
        hideSideOnMobile
        main={<HomeFeed />}
        side={
          <div className="space-y-4">
            <DiscoverCard />
            <TrendingTags />
          </div>
        }
      />
    </div>
  );
}
