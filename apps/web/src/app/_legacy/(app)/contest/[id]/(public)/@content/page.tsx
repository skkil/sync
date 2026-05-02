'use client';

import { use } from 'react';

import useGetProviderQuery from '@/features/provider/api/get-provider';

interface ContestAboutProps {
  params: Promise<{
    id: string;
  }>;
}

export default function ContestAbout({ params }: ContestAboutProps) {
  const { id } = use(params);

  const { data: contest } = useGetProviderQuery(id);

  if (!contest) {
    return null;
  }

  return <div>{contest.description}</div>;
}
