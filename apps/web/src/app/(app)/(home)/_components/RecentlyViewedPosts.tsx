import { ClockCounterClockwiseIcon } from '@phosphor-icons/react/dist/ssr';
import { getTranslations } from 'next-intl/server';

const MOCK_RECENTLY_VIEWED_POSTS = [
  {
    title: 'A field guide to our auth token lifecycle',
    subtitle: 'infra-team',
    age: '2h',
  },
  {
    title: 'Why we moved off REST for internal services',
    subtitle: 'platform',
    age: '1d',
  },
  {
    title: 'Debugging a flaky Postgres connection pool',
    subtitle: 'backend',
    age: '3d',
  },
];

export default async function RecentlyViewedPosts() {
  const t = await getTranslations('pages.home.recently-viewed');

  return (
    <div className="space-y-4 rounded-xl border bg-card p-6">
      <span className="flex items-center gap-1.5 text-sm font-semibold">
        <ClockCounterClockwiseIcon />
        {t('title')}
      </span>

      <div className="space-y-3">
        {MOCK_RECENTLY_VIEWED_POSTS.map((item) => (
          <div key={item.title} className="space-y-0.5">
            <p className="line-clamp-1 text-sm font-medium">{item.title}</p>
            <p className="text-muted-foreground text-xs">
              {item.subtitle} · {item.age}
            </p>
          </div>
        ))}
      </div>
    </div>
  );
}
