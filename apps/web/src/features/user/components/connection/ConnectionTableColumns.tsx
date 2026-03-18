'use client';

import { ColumnDef } from '@tanstack/react-table';
import Link from 'next/link';

import { DataTableColumnHeader } from '@/components/ui/data-table';

export type Connection = {
  id: string;
  name: string;
  provider: {
    id: string;
    type: string;
    name: string;
  } | null;
  profession: string | null;
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
  {
    accessorKey: 'provider',
    header: ({ column, table }) => (
      <DataTableColumnHeader
        title={table.options.meta?.t('providerName') || ''}
        column={column}
      />
    ),
    cell: ({ row }) => {
      const provider = row.original.provider;
      if (!provider) return '-';
      return <Link href={`/provider/${provider.id}`}>{provider.name}</Link>;
    },
  },
  {
    accessorKey: 'profession',
    header: ({ column, table }) => (
      <DataTableColumnHeader
        title={table.options.meta?.t('profession') || ''}
        column={column}
      />
    ),
    cell: ({ row }) => row.original.profession ?? '-',
  },
  {
    accessorKey: 'message',
    header: ({ column, table }) => (
      <DataTableColumnHeader
        title={table.options.meta?.t('message') || ''}
        column={column}
      />
    ),
    cell: ({ row, table }) => {
      const { id } = row.original;
      return (
        <Link href={`/messages?to=${id}`}>
          {table.options.meta?.t('message') || ''}
        </Link>
      );
    },
    enableSorting: false,
  },
];
