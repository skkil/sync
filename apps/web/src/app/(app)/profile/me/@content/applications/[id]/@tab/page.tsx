'use client';

import { useTranslations } from 'next-intl';
import { useParams } from 'next/navigation';

import { useGetJobApplication } from '@/api/__generated__/applications/applications';

type JobDescriptionTabParams = {
  id: string;
};

export default function JobDescriptionTab() {
  const { id: applicationId } = useParams<JobDescriptionTabParams>();

  const { data: application, isLoading } = useGetJobApplication(applicationId);

  const t = useTranslations(
    'pages.profile.applications.details.tabs.job-description',
  );

  if (isLoading) {
    return (
      <div className="space-y-2">
        <div className="h-4 w-full animate-pulse rounded bg-muted" />
        <div className="h-4 w-full animate-pulse rounded bg-muted" />
        <div className="h-4 w-3/4 animate-pulse rounded bg-muted" />
      </div>
    );
  }

  if (!application?.data) {
    return null;
  }

  const { jobDescription } = application.data;

  return (
    <div className="space-y-4">
      <div>
        <h3 className="mb-2 font-semibold">{t('job-title')}</h3>
        <p>{jobDescription.title}</p>
      </div>
      <div>
        <h3 className="mb-2 font-semibold">{t('location')}</h3>
        <p>{jobDescription.location}</p>
      </div>
      <div>
        <h3 className="mb-2 font-semibold">{t('description')}</h3>
        <div className="whitespace-pre-wrap">{jobDescription.description}</div>
      </div>
    </div>
  );
}
