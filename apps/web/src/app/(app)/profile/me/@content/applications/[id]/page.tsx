import { ArrowLeftIcon } from '@phosphor-icons/react/ssr';
import { getTranslations } from 'next-intl/server';
import Link from 'next/link';

import { Button } from '@/components/ui/button';
import { Tabs, TabsContent, TabsList, TabsTrigger } from '@/components/ui/tabs';

import ApplicationOverview from './_components/ApplicationOverview';
import JobDescriptionTab from './_components/JobDescriptionTab';
import NotesTab from './_components/NotesTab';

interface ApplicationProps {
  params: Promise<{
    id: string;
  }>;
}

export default async function Application({ params }: ApplicationProps) {
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

      <Tabs defaultValue="job-description">
        <TabsList variant="line">
          <TabsTrigger value="job-description">
            {t('tabs.job-description.label')}
          </TabsTrigger>
          <TabsTrigger value="notes">{t('tabs.notes.label')}</TabsTrigger>
        </TabsList>
        <TabsContent value="job-description">
          <JobDescriptionTab applicationId={id} />
        </TabsContent>
        <TabsContent value="notes">
          <NotesTab applicationId={id} />
        </TabsContent>
      </Tabs>
    </div>
  );
}
