'use client';

import { useTranslations } from 'next-intl';
import { useRouter, useSearchParams } from 'next/navigation';
import { useState } from 'react';

import { useGetJobPostingsByCompany } from '@/api/__generated__/jobs/jobs';
import { DataTable } from '@/components/ui/data-table';

import { columns } from './JobPostingsTableColumns';

const JOB_POSTINGS_PAGE_SIZE = 25;

interface JobPostingsProps {
  id: string;
}

export default function JobPostings({ id }: JobPostingsProps) {
  const t = useTranslations('pages.company.job-postings');
  const router = useRouter();
  const searchParams = useSearchParams();

  const [page, setPage] = useState(0);

  const {
    data: jobPostings,
    isLoading,
    isError,
  } = useGetJobPostingsByCompany(id, {
    page: page.toString(),
    size: JOB_POSTINGS_PAGE_SIZE.toString(),
  });

  const postings = jobPostings?.data?.postings;

  const handleRowClick = (row: { id: string }) => {
    const params = new URLSearchParams(searchParams);
    const queryString = params.toString();
    const url = `/company/${id}/jobs/${row.id}${queryString ? `?${queryString}` : ''}`;

    router.push(url);
  };

  return (
    <div>
      <div className="flex items-center justify-between mb-5">
        <h1 className="text-xl">{t('title')}</h1>
      </div>
      {isError ? (
        <div className="rounded-lg border border-dashed p-12 text-center">
          <p className="text-muted-foreground">{t('table.error')}</p>
        </div>
      ) : (
        <DataTable
          columns={columns}
          data={postings?.content || []}
          t={t}
          onRowClick={handleRowClick}
          currentPage={page}
          onPageChange={setPage}
          hasPreviousPage={postings?.hasPrevious}
          hasNextPage={postings?.hasNext}
          isLoading={isLoading}
        />
      )}
    </div>
  );
}
