'use client';

import { useTranslations } from 'next-intl';

import CreateJobPostingForm from './CreateJobPostingForm';
import JobPostingsTable from './JobPostingsTable';

interface JobPostingsAdminProps {
  id: string;
}

export default function JobPostingsAdmin({ id }: JobPostingsAdminProps) {
  const t = useTranslations('pages.company.job-postings');

  return (
    <div>
      <div className="flex items-center justify-between mb-5">
        <h1 className="text-xl">{t('title')}</h1>
        <CreateJobPostingForm companyId={id} />
      </div>

      <JobPostingsTable companyId={id} />
    </div>
  );
}
