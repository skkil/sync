import { MapPinIcon, SuitcaseSimpleIcon } from '@phosphor-icons/react';
import { ColumnDef } from '@tanstack/react-table';

import { DataTableColumnHeader } from '@/components/ui/data-table';

export type JobPosting = {
  id: string;
  jobTitle: string;
  jobDescription: string;
  location: string;
  createdAt: string;
};

export const columns: ColumnDef<JobPosting>[] = [
  {
    accessorKey: 'jobTitle',
    header: ({ column, table }) => (
      <DataTableColumnHeader
        icon={<SuitcaseSimpleIcon />}
        title={table.options.meta?.t('table.job-title') || ''}
        column={column}
      />
    ),
  },
  {
    accessorKey: 'location',
    header: ({ column, table }) => (
      <DataTableColumnHeader
        icon={<MapPinIcon />}
        title={table.options.meta?.t('table.location') || ''}
        column={column}
      />
    ),
  },
];
