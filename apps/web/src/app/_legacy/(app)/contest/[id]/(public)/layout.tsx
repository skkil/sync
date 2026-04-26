import { getTranslations } from 'next-intl/server';

import { ProviderTabs, Tab } from '@/features/provider/components/ProviderTabs';
import ViewProviderAsMaintainerButton from '@/features/provider/components/ViewProviderAsMaintainerButton';

import ContestOverview from './_components/ContestOverview';

interface ContestLayoutProps {
  content: React.ReactNode;
  params: Promise<{
    id: string;
  }>;
}

export default async function ContestLayout({
  content,
  params,
}: ContestLayoutProps) {
  const { id } = await params;
  const t = await getTranslations('pages.contest');

  const tabs: Tab[] = [
    {
      id: 'about',
      title: t('about.label'),
      href: `/contest/${id}`,
    },
    {
      id: 'occurrences',
      title: t('occurrences.label'),
      href: `/contest/${id}/occurrences`,
      startsWith: true,
    },
  ];

  return (
    <div className="max-w-5xl mx-auto p-4 flex flex-col gap-5">
      <ViewProviderAsMaintainerButton id={id} href={`/contest/${id}/admin`} />

      <ContestOverview id={id} />

      <div className="grow">
        <ProviderTabs tabs={tabs}>{content}</ProviderTabs>
      </div>
    </div>
  );
}
