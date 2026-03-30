'use client';

import { notFound } from 'next/navigation';
import { useEffect } from 'react';

import useGetProviderQuery from '@/features/provider/api/get-provider';
import ProviderOverview from '@/features/provider/components/ProviderOverview';
import SyncError, { ErrorCode } from '@/lib/error';
import { ProviderType } from '@/types/provider';

interface CompanyAdminOverviewProps {
  id: string;
}

export default function CompanyAdminOverview({
  id,
}: CompanyAdminOverviewProps) {
  const { data: company, isPending, error, isError } = useGetProviderQuery(id);

  useEffect(() => {
    if (isError && error instanceof SyncError) {
      switch (error.code) {
        case ErrorCode.PROVIDER_NOT_FOUND:
          notFound();
      }
    }
  }, [error, isError]);

  if (isPending || !company) {
    return null;
  }

  if (company.type !== ProviderType.COMPANY) {
    notFound();
  }

  return <ProviderOverview id={id} type={ProviderType.COMPANY} />;
}
