'use client';

import { useTranslations } from 'next-intl';

import { useGetJobApplication } from '@/api/__generated__/applications/applications';
import { Card, CardContent, CardHeader, CardTitle } from '@/components/ui/card';

export default function ApplicationOverview({
  applicationId,
}: {
  applicationId: string;
}) {
  const { data: application, isLoading } = useGetJobApplication(applicationId);
  const t = useTranslations('pages.profile.applications.details.overview');

  if (isLoading) {
    return (
      <Card>
        <CardHeader>
          <CardTitle>{t('title')}</CardTitle>
        </CardHeader>
        <CardContent>
          <div className="space-y-4">
            <div className="h-4 w-3/4 animate-pulse rounded bg-muted" />
            <div className="h-4 w-1/2 animate-pulse rounded bg-muted" />
            <div className="h-4 w-2/3 animate-pulse rounded bg-muted" />
          </div>
        </CardContent>
      </Card>
    );
  }

  if (!application?.data) {
    return null;
  }

  const { jobDescription, company, status } = application.data;

  return (
    <Card>
      <CardHeader>
        <CardTitle>{t('title')}</CardTitle>
      </CardHeader>
      <CardContent className="space-y-4">
        <div className="grid grid-cols-2 gap-4">
          <div>
            <div className="text-muted-foreground text-sm">
              {t('job-title')}
            </div>
            <div className="font-medium">{jobDescription.title}</div>
          </div>
          <div>
            <div className="text-muted-foreground text-sm">{t('company')}</div>
            <div className="font-medium">{company.name}</div>
          </div>
          <div>
            <div className="text-muted-foreground text-sm">{t('location')}</div>
            <div className="font-medium">{jobDescription.location}</div>
          </div>
          <div>
            <div className="text-muted-foreground text-sm">{t('status')}</div>
            <div className="font-medium">{status}</div>
          </div>
        </div>
      </CardContent>
    </Card>
  );
}
