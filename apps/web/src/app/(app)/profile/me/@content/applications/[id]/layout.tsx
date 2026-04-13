import {
  ArrowLeftIcon,
  FileIcon,
  InfoIcon,
  NoteBlankIcon,
} from '@phosphor-icons/react/ssr';
import { getTranslations } from 'next-intl/server';
import Link from 'next/link';

import { Button } from '@/components/ui/button';

import ApplicationOverview from './_components/ApplicationOverview';
import { JobApplicationTabs } from './_components/JobApplicationTabs';

interface ApplicationProps {
  tab: React.ReactNode;
  params: Promise<{
    id: string;
  }>;
}

export default async function Application({ tab, params }: ApplicationProps) {
  const { id } = await params;

  const t = await getTranslations('pages.profile.applications.details');

  return (
    <div className="mx-auto flex max-w-5xl flex-col gap-6">
      <div className="flex items-center gap-4">
        <Button variant="ghost" size="sm" asChild>
          <Link
            href="/profile/me/applications"
            className="flex items-center gap-2"
          >
            <ArrowLeftIcon />
            <span>{t('back-to-applications')}</span>
          </Link>
        </Button>
      </div>

      <ApplicationOverview applicationId={id} />

      <JobApplicationTabs
        tabs={[
          {
            id: 'job-description',
            icon: <InfoIcon />,
            title: t('tabs.job-description.label'),
            href: `/profile/me/applications/${id}`,
          },
          {
            id: 'notes',
            icon: <NoteBlankIcon />,
            title: t('tabs.notes.label'),
            href: `/profile/me/applications/${id}/notes`,
          },
          {
            id: 'files',
            icon: <FileIcon />,
            title: t('tabs.files.label'),
            href: `/profile/me/applications/${id}/files`,
          },
        ]}
      >
        {tab}
      </JobApplicationTabs>
    </div>
  );
}
