'use client';

import { notFound } from 'next/navigation';

import { Card, CardHeader, CardTitle } from '@/components/ui/card';
import useGetProviderQuery from '@/features/provider/api/get-provider';
import { ProviderType } from '@/types/provider';

interface ProjectOverviewProps {
  id: string;
}

export default function ProjectOverview({ id }: ProjectOverviewProps) {
  const { data: project, isPending } = useGetProviderQuery(id);

  if (isPending || !project) {
    return null;
  }

  if (project.type !== ProviderType.PROJECT) {
    notFound();
  }

  return (
    <Card>
      <CardHeader>
        <CardTitle>{project.name}</CardTitle>
      </CardHeader>
    </Card>
  );
}
