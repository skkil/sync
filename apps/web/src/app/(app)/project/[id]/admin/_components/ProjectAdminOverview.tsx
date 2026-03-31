'use client';

import { notFound } from 'next/navigation';
import { useEffect } from 'react';

import { Card, CardHeader, CardTitle } from '@/components/ui/card';
import useGetProviderQuery from '@/features/provider/api/get-provider';
import SyncError, { ErrorCode } from '@/lib/error';
import { ProviderType } from '@/types/provider';

interface ProjectOverviewProps {
  id: string;
}

export default function ProjectAdminOverview({ id }: ProjectOverviewProps) {
  const { data: project, isPending, error, isError } = useGetProviderQuery(id);

  useEffect(() => {
    if (isError && error instanceof SyncError) {
      switch (error.code) {
        case ErrorCode.PROVIDER_NOT_FOUND:
          notFound();
      }
    }
  }, [error, isError]);

  if (isPending || !project) {
    return null;
  }

  if (project.type !== ProviderType.PROJECT || !project.isMaintainer) {
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
