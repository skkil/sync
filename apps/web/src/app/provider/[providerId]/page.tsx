import { getTranslations } from 'next-intl/server';

import { Button } from '@/components/ui/button';
import {
  Card,
  CardContent,
  CardHeader,
  CardTitle,
} from '@/components/ui/card';
import { Separator } from '@/components/ui/separator';

import ReviewList from './_components/ReviewList';

const providerProfile = {
  name: 'Example Company',
  category: 'Example Category',
  location: 'Example City',
  size: 'Example Team Size',
  headline: 'Example headline',
  summary: 'Example summary text',
  stats: [
    { id: 'followers', value: '1.2k' },
    { id: 'projects', value: '12' },
    { id: 'rating', value: '4.5' },
  ],
  services: ['Example Service 1', 'Example Service 2', 'Example Service 3'],
  reviews: [
    {
      id: 'review-01',
      author: 'Example User 1',
      role: 'Example Role 1',
      rating: '5.0',
      content: 'Example review content',
    },
    {
      id: 'review-02',
      author: 'Example User 2',
      role: 'Example Role 2',
      rating: '4.7',
      content: 'Example review content',
    },
    {
      id: 'review-03',
      author: 'Example User 3',
      role: 'Example Role 3',
      rating: '4.9',
      content: 'Example review content',
    },
    {
      id: 'review-04',
      author: 'Example User 4',
      role: 'Example Role 4',
      rating: '4.6',
      content: 'Example review content',
    },
    {
      id: 'review-05',
      author: 'Example User 5',
      role: 'Example Role 5',
      rating: '4.8',
      content: 'Example review content',
    },
  ],
  people: [
    {
      id: 'person-01',
      name: 'Example Person 1',
      role: 'Example Role A',
    },
    {
      id: 'person-02',
      name: 'Example Person 2',
      role: 'Example Role B',
    },
    {
      id: 'person-03',
      name: 'Example Person 3',
      role: 'Example Role C',
    },
  ],
};

export default async function ProviderPage() {
  const t = await getTranslations('pages.provider');

  return (
    <div className="min-h-screen bg-muted/30">
      <div className="mx-auto flex w-full max-w-6xl flex-col gap-8 px-6 py-10">
        <section className="relative overflow-hidden rounded-3xl border bg-card shadow-sm">
          <div className="relative h-44 w-full bg-muted/60">
            <div className="absolute right-8 top-6 flex items-center gap-2">
              <Button variant="outline">{t('actions.follow')}</Button>
              <Button>{t('actions.review')}</Button>
            </div>
          </div>
          <div className="flex flex-col gap-5 px-8 pb-8 pt-6">
            <div className="flex flex-col gap-4 md:flex-row md:items-end md:justify-between">
              <div className="flex items-end gap-4">
                <div className="flex size-20 items-center justify-center rounded-full border-4 border-background bg-muted text-xl font-semibold text-muted-foreground">
                  EX
                </div>
                <div className="pb-1">
                  <div className="text-xs text-muted-foreground">
                    {providerProfile.category}
                  </div>
                  <h1 className="text-2xl font-semibold">
                    {providerProfile.name}
                  </h1>
                  <p className="text-sm text-muted-foreground">
                    {providerProfile.location} · {providerProfile.size}
                  </p>
                </div>
              </div>
            </div>
            <p className="text-sm text-muted-foreground">
              {providerProfile.headline}
            </p>
            <div className="flex flex-wrap gap-3">
              {providerProfile.stats.map((stat) => (
                <div
                  key={stat.id}
                  className="rounded-2xl border bg-muted/40 px-4 py-2 text-sm"
                >
                  <div className="text-xs text-muted-foreground">
                    {t(`stats.${stat.id}`)}
                  </div>
                  <div className="text-base font-semibold">{stat.value}</div>
                </div>
              ))}
            </div>
            <div className="flex flex-wrap gap-2">
              {providerProfile.services.map((service) => (
                <span
                  key={service}
                  className="rounded-full border bg-background px-3 py-1 text-xs text-muted-foreground"
                >
                  {service}
                </span>
              ))}
            </div>
          </div>
        </section>

        <section className="grid gap-6 lg:grid-cols-[1.6fr_1fr]">
          <Card>
            <CardHeader>
              <CardTitle>{t('sections.about')}</CardTitle>
            </CardHeader>
            <CardContent className="space-y-4 text-sm text-muted-foreground">
              <p>{providerProfile.summary}</p>
              <Separator />
              <div className="grid gap-3 md:grid-cols-2">
                {providerProfile.services.map((service) => (
                  <div
                    key={service}
                    className="rounded-2xl border bg-muted/40 px-4 py-3 text-sm text-foreground"
                  >
                    {service}
                  </div>
                ))}
              </div>
            </CardContent>
          </Card>

          <Card>
            <CardHeader>
              <CardTitle>{t('sections.people')}</CardTitle>
            </CardHeader>
            <CardContent className="space-y-4">
              {providerProfile.people.map((person) => (
                <div
                  key={person.id}
                  className="flex items-center justify-between rounded-2xl border bg-muted/30 px-4 py-3"
                >
                  <div>
                    <div className="text-sm font-medium">{person.name}</div>
                    <div className="text-xs text-muted-foreground">
                      {person.role}
                    </div>
                  </div>
                  <Button variant="ghost" size="sm">
                    {t('actions.view')}
                  </Button>
                </div>
              ))}
            </CardContent>
          </Card>
        </section>

        <Card>
          <CardHeader>
            <CardTitle>{t('sections.reviews')}</CardTitle>
          </CardHeader>
          <CardContent>
            <ReviewList
              reviews={providerProfile.reviews}
              moreLabel={t('actions.moreReviews')}
              collapseLabel={t('actions.collapseReviews')}
            />
          </CardContent>
        </Card>
      </div>
    </div>
  );
}