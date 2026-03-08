import { getTranslations } from 'next-intl/server';

import ProviderAdminPage from '@/features/provider/components/ProviderAdminPage';
import JobPostingsAdmin from '@/features/provider/components/company/JobPostingsAdmin';
import { ProviderType } from '@/types/provider';

interface CompanyProps {
  params: Promise<{
    id: string;
  }>;
}

export default async function CompanyAdmin({ params }: CompanyProps) {
  const t = await getTranslations('pages.provider-admin');

  const { id } = await params;

  return (
    <ProviderAdminPage
      id={id}
      providerType={ProviderType.COMPANY}
      additionalTabs={[
        {
          id: 'jobs',
          title: t('sections.job-postings'),
          content: <JobPostingsAdmin id={id} />,
        },
      ]}
    />
  );
}
