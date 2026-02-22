import { EyeIcon } from '@phosphor-icons/react/dist/ssr';
import { HydrationBoundary, dehydrate } from '@tanstack/react-query';
import { getTranslations } from 'next-intl/server';
import Link from 'next/link';
import { notFound, redirect } from 'next/navigation';

import { Button } from '@/components/ui/button';
import { Card } from '@/components/ui/card';
import { Tabs, TabsContent, TabsList, TabsTrigger } from '@/components/ui/tabs';
import { GetProviderQueryOptions } from '@/features/provider/api/get-provider';
import ProviderAbout from '@/features/provider/components/ProviderAbout';
import ProviderOverview from '@/features/provider/components/ProviderOverview';
import SyncError, { ErrorCode } from '@/lib/error';
import { getQueryClient } from '@/lib/query';
import { ProviderType } from '@/types/provider';

interface SchoolProps {
  params: Promise<{
    id: string;
  }>;
}

export default async function SchoolAdmin({ params }: SchoolProps) {
  const { id } = await params;
  const t = await getTranslations('pages.provider-admin');

  const queryClient = getQueryClient();
  const provider = await queryClient
    .fetchQuery(GetProviderQueryOptions(id))
    .catch((error) => {
      if (error instanceof SyncError) {
        switch (error.code) {
          case ErrorCode.PROVIDER_NOT_FOUND:
            notFound();
        }
      }

      throw error;
    });

  if (!provider || !provider.isMaintainer) {
    redirect(`/school/${id}`);
  }

  const tabs = [
    {
      id: 'about',
      title: t('sections.about'),
      content: <ProviderAbout />,
    },
    {
      id: 'settings',
      title: t('sections.settings'),
      content: <div></div>,
    },
  ];

  return (
    <HydrationBoundary state={dehydrate(queryClient)}>
      <div className="max-w-5xl mx-auto p-4 flex flex-col gap-5">
        <ProviderOverview id={id} type={ProviderType.SCHOOL} />

        <div className="grow">
          <Tabs defaultValue={tabs[0]?.id} className="w-full">
            <div className="flex items-center justify-between">
              <TabsList>
                {tabs.map((tab) => (
                  <TabsTrigger key={tab.id} value={tab.id}>
                    {tab.title}
                  </TabsTrigger>
                ))}
              </TabsList>

              <Link href={`/school/${id}?m=true`}>
                <Button variant="outline" asChild>
                  <EyeIcon />
                  {t('view-as-member')}
                </Button>
              </Link>
            </div>

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
