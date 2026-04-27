import { getTranslations } from 'next-intl/server';

import { ProviderTabs } from '@/features/provider/components/ProviderTabs';
import ViewProviderAsPublicButton from '@/features/provider/components/ViewProviderAsPublicButton';

import CompanyAdminOverview from './_components/CompanyAdminOverview';

interface CompanyAdminLayoutProps {
  content: React.ReactNode;
  params: Promise<{
    id: string;
  }>;
}

export default async function CompanyAdminLayout({
  content,
  params,
}: CompanyAdminLayoutProps) {
  const { id } = await params;
  const t = await getTranslations('pages.company');

  const tabs: {
    id: string;
    title: string;
    href: string;
  }[] = [
    {
      id: 'about',
      title: t('about.label'),
      href: `/company/${id}/admin`,
    },
    {
      id: 'job-postings',
      title: t('job-postings.label'),
      href: `/company/${id}/admin/jobs`,
    },
  ];

  return (
    <div className="max-w-5xl mx-auto p-4 flex flex-col gap-5">
      <CompanyAdminOverview id={id} />
      <div className="grow">
        <ProviderTabs
          tabs={tabs}
          right={<ViewProviderAsPublicButton href={`/company/${id}?m=true`} />}
        >
          {content}
        </ProviderTabs>
      </div>
    </div>
  );
}
