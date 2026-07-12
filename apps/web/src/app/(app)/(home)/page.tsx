import { getTranslations } from 'next-intl/server';

import { TwoColumnLayout } from '@/components/layout/TwoColumnLayout';
import { Unimplemented } from '@/components/ui/unimplemented';
import { requireOnboardedSession } from '@/lib/auth/guards';

import DiscoverCard from './_components/DiscoverCard';
import HomeFeed from './_components/HomeFeed';
import RecentlyViewedPosts from './_components/RecentlyViewedPosts';
import TrendingTags from './_components/TrendingTags';

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
            <Unimplemented>
              <DiscoverCard />
            </Unimplemented>
            <Unimplemented>
              <TrendingTags />
            </Unimplemented>
            <Unimplemented>
              <RecentlyViewedPosts />
            </Unimplemented>
          </div>
        }
      />
    </div>
  );
}
