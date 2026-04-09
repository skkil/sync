'use client';

import { use } from 'react';

import useGetProviderQuery from '@/features/provider/api/get-provider';

interface ContestAdminAboutProps {
  params: Promise<{
    id: string;
  }>;
}

export default function ContestAdminAbout({ params }: ContestAdminAboutProps) {
  const { id } = use(params);

  const { data: contest } = useGetProviderQuery(id);

  if (!contest) {
    return null;
  }

  return <div>{contest.description}</div>;
}
