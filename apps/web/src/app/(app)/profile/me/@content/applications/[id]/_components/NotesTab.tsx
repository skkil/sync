'use client';

import { useTranslations } from 'next-intl';

import { useGetJobApplication } from '@/api/__generated__/applications/applications';
import { Card, CardContent, CardHeader, CardTitle } from '@/components/ui/card';

export default function NotesTab({ applicationId }: { applicationId: string }) {
  const { data: application, isLoading } = useGetJobApplication(applicationId);
  const t = useTranslations('pages.profile.applications.details.tabs.notes');

  if (isLoading) {
    return (
      <Card>
        <CardHeader>
          <CardTitle>{t('title')}</CardTitle>
        </CardHeader>
        <CardContent>
          <div className="space-y-2">
            <div className="h-4 w-full animate-pulse rounded bg-muted" />
            <div className="h-4 w-3/4 animate-pulse rounded bg-muted" />
          </div>
        </CardContent>
      </Card>
    );
  }

  if (!application?.data) {
    return null;
  }

  const { notes } = application.data;

  return (
    <Card>
      <CardHeader>
        <CardTitle>{t('title')}</CardTitle>
      </CardHeader>
      <CardContent>
        {notes ? (
          <div className="whitespace-pre-wrap">{notes}</div>
        ) : (
          <p className="text-muted-foreground">{t('empty')}</p>
        )}
      </CardContent>
    </Card>
  );
}
