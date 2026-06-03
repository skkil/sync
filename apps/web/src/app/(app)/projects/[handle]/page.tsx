import { HydrationBoundary, dehydrate } from '@tanstack/react-query';

import { getGetProjectByHandleQueryOptions } from '@/api/__generated__/project/project';
import { getQueryClient } from '@/lib/query';

import ProjectOverview from './_components/ProjectOverview';

interface ProjectPageProps {
  params: Promise<{
    handle: string;
  }>;
}

export default async function ProjectPage({ params }: ProjectPageProps) {
  const { handle } = await params;

  const queryClient = getQueryClient();
  await queryClient.prefetchQuery(getGetProjectByHandleQueryOptions(handle));

  return (
    <HydrationBoundary state={dehydrate(queryClient)}>
      <div className="container max-w-3xl py-8">
        <ProjectOverview handle={handle} />
      </div>
    </HydrationBoundary>
  );
}
