import { getTranslations } from 'next-intl/server';

import { ProviderTabs } from '@/features/provider/components/ProviderTabs';
import ViewProviderAsPublicButton from '@/features/provider/components/ViewProviderAsPublicButton';

interface ProjectAdminLayoutProps {
  content: React.ReactNode;
  params: Promise<{
    id: string;
  }>;
}

export default async function ProjectAdminLayout({
  content,
  params,
}: ProjectAdminLayoutProps) {
  const { id } = await params;
  const t = await getTranslations('pages.project.admin');

  const tabs: {
    id: string;
    title: string;
    href: string;
  }[] = [
    {
      id: 'about',
      title: t('about.label'),
      href: `/project/${id}/admin`,
    },
    {
      id: 'team-building',
      title: t('team-building.label'),
      href: `/project/${id}/admin/teams`,
    },
  ];

  return (
    <div className="max-w-5xl mx-auto p-4 flex flex-col gap-5">
      <div className="grow">
        <ProviderTabs
          tabs={tabs}
          right={<ViewProviderAsPublicButton href={`/project/${id}?m=true`} />}
        >
          {content}
        </ProviderTabs>
      </div>
    </div>
  );
}
