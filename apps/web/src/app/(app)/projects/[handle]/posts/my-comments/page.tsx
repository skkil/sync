import { HydrationBoundary, dehydrate } from '@tanstack/react-query';
import { notFound } from 'next/navigation';

import { getGetProjectByHandleQueryOptions } from '@/api/__generated__/project/project';
import { requireOnboardedSession } from '@/lib/auth/guards';
import SyncError, { ErrorCode } from '@/lib/error';
import { getQueryClient } from '@/lib/query';

import ProjectMyComments from '../_components/ProjectMyComments';

interface ProjectMyCommentsPageProps {
  params: Promise<{
    handle: string;
  }>;
}

export default async function ProjectMyCommentsPage({
  params,
}: ProjectMyCommentsPageProps) {
  const { handle } = await params;
  await requireOnboardedSession();

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
      <ProjectMyComments handle={handle} />
    </HydrationBoundary>
  );
}
