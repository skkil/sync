'use client';

import { notFound } from 'next/navigation';
import { useEffect } from 'react';

import { Card, CardHeader, CardTitle } from '@/components/ui/card';
import useGetProviderQuery from '@/features/provider/api/get-provider';
import SyncError, { ErrorCode } from '@/lib/error';
import { ProviderType } from '@/types/provider';

interface ContestOverviewProps {
  id: string;
}

export default function ContestOverview({ id }: ContestOverviewProps) {
  const { data: contest, isPending, error, isError } = useGetProviderQuery(id);

  useEffect(() => {
    if (isError && error instanceof SyncError) {
      switch (error.code) {
        case ErrorCode.PROVIDER_NOT_FOUND:
          notFound();
      }
    }
  }, [error, isError]);

  if (isPending || !contest) {
    return null;
  }

  if (contest.type !== ProviderType.CONTEST) {
    notFound();
  }

  return (
    <Card>
      <CardHeader>
        <CardTitle>{contest.name}</CardTitle>
      </CardHeader>
    </Card>
  );
}
