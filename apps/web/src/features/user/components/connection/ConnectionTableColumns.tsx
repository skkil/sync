'use client';

import { ColumnDef } from '@tanstack/react-table';
import Link from 'next/link';

import { DataTableColumnHeader } from '@/components/ui/data-table';

export type Connection = {
  id: string;
  name: string;
};

export const columns: ColumnDef<Connection>[] = [
  {
    accessorKey: 'name',
    header: ({ column, table }) => (
      <DataTableColumnHeader
        title={table.options.meta?.t('name') || ''}
        column={column}
      />
    ),
    cell: ({ row }) => {
      const { id, name } = row.original;
      return <Link href={`/profile/${id}`}>{name}</Link>;
    },
  },
];
