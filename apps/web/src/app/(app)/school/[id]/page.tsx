import { HydrationBoundary, dehydrate } from '@tanstack/react-query';
import { getTranslations } from 'next-intl/server';
import Link from 'next/link';
import { notFound, redirect } from 'next/navigation';

import { Button } from '@/components/ui/button';
import {
  Card,
  CardDescription,
  CardHeader,
  CardTitle,
} from '@/components/ui/card';
import { Tabs, TabsContent, TabsList, TabsTrigger } from '@/components/ui/tabs';
import { GetProviderQueryOptions } from '@/features/provider/api/get-provider';
import ProviderAbout from '@/features/provider/components/ProviderAbout';
import ProviderOverview from '@/features/provider/components/ProviderOverview';
import ProviderRelatedPeople from '@/features/provider/components/ProviderPeople';
import ProviderReviews from '@/features/provider/components/ProviderReviews';
import SyncError, { ErrorCode } from '@/lib/error';
import { getQueryClient } from '@/lib/query';
import { ProviderType } from '@/types/provider';

interface SchoolProps {
  params: Promise<{
    id: string;
  }>;
  searchParams: Promise<{ [key: string]: string | string[] | undefined }>;
}

export default async function School({ params, searchParams }: SchoolProps) {
  const { id } = await params;
  const t = await getTranslations('pages.provider');

  const viewAsMember =
    (await searchParams.then((params) => params.m)) === 'true';

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

  if (!viewAsMember && provider?.isMaintainer) {
    redirect(`/school/${id}/admin`);
  }

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
        {provider?.isMaintainer && (
          <Card>
            <CardHeader>
              <div className="flex items-center justify-between">
                <div>
                  <CardTitle>
                    <span>
                      {t('admin.viewing-as-member.title', {
                        name: provider.name,
                      })}
                    </span>
                  </CardTitle>

                  <CardDescription>
                    {t('admin.viewing-as-member.description')}
                  </CardDescription>
                </div>

                <Button asChild>
                  <Link href={`/school/${id}/admin`}>
                    {t('admin.viewing-as-member.view-as-admin')}
                  </Link>
                </Button>
              </div>
            </CardHeader>
          </Card>
        )}

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
