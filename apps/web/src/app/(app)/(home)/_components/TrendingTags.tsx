import { TrendUpIcon } from '@phosphor-icons/react/dist/ssr';
import { getTranslations } from 'next-intl/server';
import Link from 'next/link';

import ROUTES from '@/util/routes';

const MOCK_TRENDING_TAGS = [
  { name: 'kubernetes', postCount: 482 },
  { name: 'observability', postCount: 317 },
  { name: 'rust', postCount: 265 },
];

export default async function TrendingTags() {
  const t = await getTranslations('pages.home.trending-tags');

  return (
    <div className="space-y-4 rounded-xl border bg-card p-6">
      <span className="flex items-center gap-1.5 text-sm font-semibold">
        <TrendUpIcon />
        {t('title')}
      </span>

      <div className="divide-border -mx-2 divide-y">
        {MOCK_TRENDING_TAGS.map((tag) => (
          <Link
            key={tag.name}
            href={ROUTES.EXPLORE_TRENDING()}
            className="hover:bg-accent/50 block rounded-md px-2 py-2"
          >
            <p className="text-sm font-semibold">#{tag.name}</p>
            <p className="text-muted-foreground text-xs">
              {t('post-count', { count: tag.postCount })}
            </p>
          </Link>
        ))}
      </div>
    </div>
  );
}
