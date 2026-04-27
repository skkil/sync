'use client';

import { use } from 'react';

import CreateJobPostingForm from './_components/CreateJobPostingForm';

interface CompanyAdminJobsProps {
  params: Promise<{
    id: string;
  }>;
}

export default function CompanyAdminJobs({ params }: CompanyAdminJobsProps) {
  const { id } = use(params);

  return <CreateJobPostingForm companyId={id} />;
}
