'use client';

import { use } from 'react';

import ProviderAbout from '@/features/provider/components/ProviderAbout';

interface CompanyAboutProps {
  params: Promise<{
    id: string;
  }>;
}

export default function CompanyAbout({ params }: CompanyAboutProps) {
  const { id } = use(params);

  return <ProviderAbout id={id} />;
}
