'use client';

import { useTranslations } from 'next-intl';

import JobPostingsTable from './JobPostingsTable';

interface JobPostingsProps {
  id: string;
}

export default function JobPostings({ id }: JobPostingsProps) {
  const t = useTranslations('pages.company.job-postings');

  return (
    <div>
      <div className="flex items-center justify-between mb-5">
        <h1 className="text-xl">{t('title')}</h1>
      </div>

      <JobPostingsTable companyId={id} />
    </div>
  );
}
