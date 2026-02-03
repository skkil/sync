import { HydrationBoundary, dehydrate } from '@tanstack/react-query';
import { getTranslations } from 'next-intl/server';

import { Card } from '@/components/ui/card';
import { Tabs, TabsContent, TabsList, TabsTrigger } from '@/components/ui/tabs';
import { GetProviderQueryOptions } from '@/features/provider/api/get-provider';
import ProviderAbout from '@/features/provider/components/ProviderAbout';
import ProviderOverview from '@/features/provider/components/ProviderOverview';
import ProviderRelatedPeople from '@/features/provider/components/ProviderPeople';
import ProviderReviews from '@/features/provider/components/ProviderReviews';
import { getQueryClient } from '@/lib/query';
import { ProviderType } from '@/types/provider';

interface SchoolProps {
  params: Promise<{
    id: string;
  }>;
}

export default async function School({ params }: SchoolProps) {
  const { id } = await params;
  const t = await getTranslations('pages.provider');

  const queryClient = getQueryClient();
  await queryClient.prefetchQuery(GetProviderQueryOptions(id));

  const tabs = [
    {
      id: 'about',
      title: t('sections.about'),
      content: <ProviderAbout />,
    },
    {
      id: 'people',
      title: t('sections.people'),
      content: <ProviderRelatedPeople />,
    },
    {
      id: 'reviews',
      title: t('sections.reviews'),
      content: <ProviderReviews />,
    },
  ];

  return (
    <HydrationBoundary state={dehydrate(queryClient)}>
      <div className="max-w-5xl mx-auto p-4 flex flex-col gap-5">
        <ProviderOverview id={id} type={ProviderType.SCHOOL} />

        <div className="grow">
          <Tabs defaultValue={tabs[0]?.id} className="w-full">
            <TabsList>
              {tabs.map((tab) => (
                <TabsTrigger key={tab.id} value={tab.id}>
                  {tab.title}
                </TabsTrigger>
              ))}
            </TabsList>

            <Card className="min-h-96 overflow-auto">
              {tabs.map((tab) => (
                <TabsContent key={tab.id} value={tab.id}>
                  {tab.content}
                </TabsContent>
              ))}
            </Card>
          </Tabs>
        </div>
      </div>
    </HydrationBoundary>
  );
}
