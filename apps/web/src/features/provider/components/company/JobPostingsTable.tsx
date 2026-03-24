import { useTranslations } from 'next-intl';
import { useState } from 'react';

import { DataTable } from '@/components/ui/data-table';

import { useGetJobPostingsByCompanyQuery } from '../../api/company/get-job-postings';
import JobPostingModal from './JobPostingModal';
import { JobPosting, columns } from './JobPostingsTableColumns';

interface JobPostingsTableProps {
  companyId: string;
}

export default function JobPostingsTable({ companyId }: JobPostingsTableProps) {
  const t = useTranslations('pages.company.job-postings.table');

  const { data: jobPostings } = useGetJobPostingsByCompanyQuery(companyId);

  const [open, setOpen] = useState(false);
  const [selectedJobPosting, setSelectedJobPosting] =
    useState<JobPosting | null>(null);

  return (
    <>
      <DataTable
        columns={columns}
        data={jobPostings?.content || []}
        t={t}
        onRowClick={(row) => {
          setSelectedJobPosting(row);
          setOpen(true);
        }}
      />

      <JobPostingModal
        jobPosting={selectedJobPosting}
        open={open}
        onOpenChange={setOpen}
      />
    </>
  );
}
