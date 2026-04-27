'use client';

import { useTranslations } from 'next-intl';
import { useParams } from 'next/navigation';

import { useGetJobApplication } from '@/api/__generated__/applications/applications';

type NotesTabParams = {
  id: string;
};

export default function NotesTab() {
  const { id: applicationId } = useParams<NotesTabParams>();

  const { data: application, isLoading } = useGetJobApplication(applicationId);

  const t = useTranslations('pages.profile.applications.details.tabs.notes');

  if (isLoading) {
    return (
      <div className="space-y-2">
        <div className="h-4 w-full animate-pulse rounded bg-muted" />
        <div className="h-4 w-3/4 animate-pulse rounded bg-muted" />
      </div>
    );
  }

  if (!application?.data) {
    return null;
  }

  const { notes } = application.data;

  return (
    <div>
      {notes ? (
        <div className="whitespace-pre-wrap">{notes}</div>
      ) : (
        <p className="text-muted-foreground">{t('empty')}</p>
      )}
    </div>
  );
}
