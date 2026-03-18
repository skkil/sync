import { HydrationBoundary, dehydrate } from '@tanstack/react-query';
import { getTranslations } from 'next-intl/server';
import { notFound, redirect } from 'next/navigation';
import { ReactNode } from 'react';

import { GetProviderQueryOptions } from '@/features/provider/api/get-provider';
import ProviderAbout from '@/features/provider/components/ProviderAbout';
import ProviderAdminLayout from '@/features/provider/components/ProviderAdminLayout';
import ProviderOverview from '@/features/provider/components/ProviderOverview';
import SyncError, { ErrorCode } from '@/lib/error';
import { getQueryClient } from '@/lib/query';
import { ProviderType } from '@/types/provider';

interface Tab {
  id: string;
  title: string;
  content: ReactNode;
}

interface ProviderAdminPageProps {
  id: string;
  providerType: ProviderType;
  additionalTabs?: Tab[];
}

export default async function ProviderAdminPage({
  id,
  providerType,
  additionalTabs = [],
}: ProviderAdminPageProps) {
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

  const providerPath = getProviderPath(providerType, id);

  if (!provider || !provider.isMaintainer) {
    redirect(providerPath);
  }

  const tabs = [
    {
      id: 'about',
      title: t('sections.about'),
      content: <ProviderAbout id={id} showEditButton />,
    },
    ...additionalTabs,
    {
      id: 'settings',
      title: t('sections.settings'),
      content: <div></div>,
    },
  ];

  return (
    <HydrationBoundary state={dehydrate(queryClient)}>
      <ProviderAdminLayout tabs={tabs} viewAsPath={`${providerPath}?m=true`}>
        <ProviderOverview id={id} type={providerType} />
      </ProviderAdminLayout>
    </HydrationBoundary>
  );
}

function getProviderPath(providerType: ProviderType, id: string) {
  switch (providerType) {
    case ProviderType.SCHOOL:
      return `/school/${id}`;
    case ProviderType.COMPANY:
      return `/company/${id}`;
    case ProviderType.PROJECT:
      return `/project/${id}`;
    default:
      console.warn('Unknown provider type:', providerType);
      return '/';
  }
}
