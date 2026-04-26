import { getTranslations } from 'next-intl/server';

import { ProviderTabs } from '@/features/provider/components/ProviderTabs';
import ViewProviderAsPublicButton from '@/features/provider/components/ViewProviderAsPublicButton';

import ContestAdminOverview from './_components/ContestAdminOverview';

interface ContestAdminLayoutProps {
  content: React.ReactNode;
  params: Promise<{
    id: string;
  }>;
}

export default async function ContestAdminLayout({
  content,
  params,
}: ContestAdminLayoutProps) {
  const { id } = await params;
  const t = await getTranslations('pages.contest');

  const tabs: {
    id: string;
    title: string;
    href: string;
  }[] = [
    {
      id: 'about',
      title: t('about.label'),
      href: `/contest/${id}/admin`,
    },
    {
      id: 'occurrences',
      title: t('occurrences.label'),
      href: `/contest/${id}/admin/occurrences`,
    },
  ];

  return (
    <div className="max-w-5xl mx-auto p-4 flex flex-col gap-5">
      <ContestAdminOverview id={id} />
      <div className="grow">
        <ProviderTabs
          tabs={tabs}
          right={<ViewProviderAsPublicButton href={`/contest/${id}?m=true`} />}
        >
          {content}
        </ProviderTabs>
      </div>
    </div>
  );
}
