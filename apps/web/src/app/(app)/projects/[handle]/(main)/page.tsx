import { HydrationBoundary, dehydrate } from '@tanstack/react-query';
import { notFound } from 'next/navigation';

import { getGetProjectByHandleQueryOptions } from '@/api/__generated__/project/project';
import SyncError, { ErrorCode } from '@/lib/error';
import { getQueryClient } from '@/lib/query';

import ProjectPosts from './_components/ProjectPosts';

interface ProjectPageProps {
  params: Promise<{
    handle: string;
  }>;
}

export default async function ProjectPage({ params }: ProjectPageProps) {
  const { handle } = await params;

  const queryClient = getQueryClient();

  try {
    await queryClient.fetchQuery(getGetProjectByHandleQueryOptions(handle));
  } catch (error) {
    if (error instanceof SyncError) {
      switch (error.code) {
        case ErrorCode.PROJECT_NOT_FOUND:
          notFound();
      }
    }
  }

  return (
    <HydrationBoundary state={dehydrate(queryClient)}>
      <div className="container max-w-3xl py-8 space-y-6">
        <ProjectPosts handle={handle} />
      </div>
    </HydrationBoundary>
  );
}
