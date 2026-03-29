import { useTranslations } from 'next-intl';
import { useState } from 'react';

import { useGetJobPostingsByCompany } from '@/api/__generated__/jobs/jobs';
import { DataTable } from '@/components/ui/data-table';

import JobPostingModal from './JobPostingModal';
import { JobPosting, columns } from './JobPostingsTableColumns';

const JOB_POSTINGS_PAGE_SIZE = 25;

interface JobPostingsTableProps {
  companyId: string;
}

export default function JobPostingsTable({ companyId }: JobPostingsTableProps) {
  const t = useTranslations('pages.company.job-postings.table');
  const [page, setPage] = useState(0);

  const {
    data: jobPostings,
    isLoading,
    isError,
  } = useGetJobPostingsByCompany(companyId, {
    page: page.toString(),
    size: JOB_POSTINGS_PAGE_SIZE.toString(),
  });

  const postings = jobPostings?.data?.postings;

  const [open, setOpen] = useState(false);
  const [selectedJobPosting, setSelectedJobPosting] =
    useState<JobPosting | null>(null);

  if (isError) {
    return (
      <div className="rounded-lg border border-dashed p-12 text-center">
        <p className="text-muted-foreground">{t('error')}</p>
      </div>
    );
  }

  return (
    <>
      <DataTable
        columns={columns}
        data={postings?.content || []}
        t={t}
        onRowClick={(row) => {
          setSelectedJobPosting(row);
          setOpen(true);
        }}
        currentPage={page}
        onPageChange={setPage}
        hasPreviousPage={postings?.hasPrevious}
        hasNextPage={postings?.hasNext}
        isLoading={isLoading}
      />

      <JobPostingModal
        jobPosting={selectedJobPosting}
        open={open}
        onOpenChange={setOpen}
      />
    </>
  );
}
