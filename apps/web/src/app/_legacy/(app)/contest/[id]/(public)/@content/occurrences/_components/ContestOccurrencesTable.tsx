'use client';

import { CalendarIcon } from '@phosphor-icons/react';
import { ColumnDef } from '@tanstack/react-table';
import { useTranslations } from 'next-intl';
import { useRouter, useSearchParams } from 'next/navigation';
import { useState } from 'react';

import { useGetContestOccurrencesByContest } from '@/api/__generated__/contests/contests';
import { GetContestOccurrencesResponseOccurrencesContentItem } from '@/api/__generated__/types';
import { DataTable, DataTableColumnHeader } from '@/components/ui/data-table';

const OCCURRENCES_PAGE_SIZE = 25;

interface ContestOccurrencesTableProps {
  contestId: string;
}

type OccurrenceRow = GetContestOccurrencesResponseOccurrencesContentItem;

const columns: ColumnDef<OccurrenceRow>[] = [
  {
    accessorKey: 'title',
    header: ({ column, table }) => (
      <DataTableColumnHeader
        icon={<CalendarIcon />}
        title={table.options.meta?.t('table.title') || ''}
        column={column}
      />
    ),
  },
];

export default function ContestOccurrencesTable({
  contestId,
}: ContestOccurrencesTableProps) {
  const t = useTranslations('pages.contest.occurrences');
  const router = useRouter();
  const searchParams = useSearchParams();

  const [page, setPage] = useState(0);

  const {
    data: occurrencesData,
    isLoading,
    isError,
  } = useGetContestOccurrencesByContest(contestId, {
    page: page.toString(),
    size: OCCURRENCES_PAGE_SIZE.toString(),
  });

  const occurrences = occurrencesData?.data?.occurrences;

  const handleRowClick = (row: OccurrenceRow) => {
    const params = new URLSearchParams(searchParams);
    const queryString = params.toString();
    const url = `/contest/${contestId}/occurrences/${row.id}${queryString ? `?${queryString}` : ''}`;

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
          data={occurrences?.content || []}
          t={t}
          onRowClick={handleRowClick}
          currentPage={page}
          onPageChange={setPage}
          hasPreviousPage={occurrences?.pageInfo.hasPreviousPage}
          hasNextPage={occurrences?.pageInfo.hasNextPage}
          isLoading={isLoading}
        />
      )}
    </div>
  );
}
