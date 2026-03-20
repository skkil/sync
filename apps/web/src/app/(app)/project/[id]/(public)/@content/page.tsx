'use client';

import { use } from 'react';

import useGetProviderQuery from '@/features/provider/api/get-provider';

interface ProjectAboutProps {
  params: Promise<{
    id: string;
  }>;
}

export default function ProjectAbout({ params }: ProjectAboutProps) {
  const { id } = use(params);

  const { data: project } = useGetProviderQuery(id);

  if (!project) {
    return null;
  }

  return <div>{project.description}</div>;
}
