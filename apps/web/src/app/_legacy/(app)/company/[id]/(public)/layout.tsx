import { getTranslations } from 'next-intl/server';

import { ProviderTabs, Tab } from '@/features/provider/components/ProviderTabs';
import ViewProviderAsMaintainerButton from '@/features/provider/components/ViewProviderAsMaintainerButton';

import CompanyOverview from './_components/CompanyOverview';

interface CompanyLayoutProps {
  content: React.ReactNode;
  params: Promise<{
    id: string;
  }>;
}

export default async function CompanyLayout({
  content,
  params,
}: CompanyLayoutProps) {
  const { id } = await params;
  const t = await getTranslations('pages.company');

  const tabs: Tab[] = [
    {
      id: 'about',
      title: t('about.label'),
      href: `/company/${id}`,
    },
    {
      id: 'jobs',
      title: t('job-postings.label'),
      href: `/company/${id}/jobs`,
      startsWith: true,
    },
  ];

  return (
    <div className="max-w-5xl mx-auto p-4 flex flex-col gap-5">
      <ViewProviderAsMaintainerButton id={id} href={`/company/${id}/admin`} />

      <CompanyOverview id={id} />

      <div className="grow">
        <ProviderTabs tabs={tabs}>{content}</ProviderTabs>
      </div>
    </div>
  );
}
