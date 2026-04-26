'use client';

import { use } from 'react';

import JobPostings from './_components/JobPostings';

interface CompanyJobsProps {
  params: Promise<{
    id: string;
  }>;
}

export default function CompanyJobs({ params }: CompanyJobsProps) {
  const { id } = use(params);

  return <JobPostings id={id} />;
}
