'use client';

import { useTranslations } from 'next-intl';

import { DataTable } from '@/components/ui/data-table';
import { useGetConnectionsQuery } from '@/features/user/api/get-connections';
import { columns } from '@/features/user/components/connection/ConnectionTableColumns';

export default function ConnectionsTable() {
  const t = useTranslations('pages.network.connections.table');

  const { data: connections } = useGetConnectionsQuery();

  return <DataTable columns={columns} data={connections || []} t={t} />;
}
