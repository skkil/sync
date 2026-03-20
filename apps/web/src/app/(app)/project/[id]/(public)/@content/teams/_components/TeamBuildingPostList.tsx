'use client';

import { ColumnDef } from '@tanstack/react-table';
import { useTranslations } from 'next-intl';

import { DataTable, DataTableColumnHeader } from '@/components/ui/data-table';
import useGetProjectTeamBuildingPostsQuery from '@/features/provider/api/project/get-project-team-building-posts';

type TeamBuildingPost = {
  id: string;
  title: string;
  content: string;
};

export const columns: ColumnDef<TeamBuildingPost>[] = [
  {
    accessorKey: 'title',
    header: ({ column, table }) => (
      <DataTableColumnHeader
        title={table.options.meta?.t('title') || ''}
        column={column}
      />
    ),
  },
];

interface TeamBuildingPostsListProps {
  projectId: string;
}

export default function TeamBuildingPostList({
  projectId,
}: TeamBuildingPostsListProps) {
  const t = useTranslations('pages.project.team-building.list');

  const { data: posts } = useGetProjectTeamBuildingPostsQuery(projectId);

  return <DataTable columns={columns} data={posts || []} t={t} />;
}
