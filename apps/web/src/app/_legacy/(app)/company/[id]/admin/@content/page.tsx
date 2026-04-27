'use client';

import { use } from 'react';

import ProviderAbout from '@/features/provider/components/ProviderAbout';

interface CompanyAdminAboutProps {
  params: Promise<{
    id: string;
  }>;
}

export default function CompanyAdminAbout({ params }: CompanyAdminAboutProps) {
  const { id } = use(params);

  return <ProviderAbout id={id} showEditButton />;
}
