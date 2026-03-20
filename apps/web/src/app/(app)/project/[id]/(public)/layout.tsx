import { getTranslations } from 'next-intl/server';

import { ProviderTabs } from '@/features/provider/components/ProviderTabs';
import ViewProviderAsMaintainerButton from '@/features/provider/components/ViewProviderAsMaintainerButton';

import ProjectOverview from './_components/ProjectOverview';

interface ProjectLayoutProps {
  content: React.ReactNode;
  params: Promise<{
    id: string;
  }>;
}

export default async function ProjectLayout({
  content,
  params,
}: ProjectLayoutProps) {
  const { id } = await params;
  const t = await getTranslations('pages.project');

  const tabs: {
    id: string;
    title: string;
    href: string;
  }[] = [
    {
      id: 'about',
      title: t('about.label'),
      href: `/project/${id}`,
    },
    {
      id: 'team-building',
      title: t('team-building.label'),
      href: `/project/${id}/teams`,
    },
  ];

  return (
    <div className="max-w-5xl mx-auto p-4 flex flex-col gap-5">
      <ViewProviderAsMaintainerButton id={id} href={`/project/${id}/admin`} />

      <ProjectOverview id={id} />

      <div className="grow">
        <ProviderTabs tabs={tabs}>{content}</ProviderTabs>
      </div>
    </div>
  );
}
