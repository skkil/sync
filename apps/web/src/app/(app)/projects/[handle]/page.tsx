import { HydrationBoundary, dehydrate } from '@tanstack/react-query';

import { getGetProjectByHandleQueryOptions } from '@/api/__generated__/project/project';
import { getGetReflectionsByProjectInfiniteQueryOptions } from '@/api/__generated__/reflection/reflection';
import { getQueryClient } from '@/lib/query';

import ProjectOverview from './_components/ProjectOverview';
import ProjectPosts from './_components/ProjectPosts';

interface ProjectPageProps {
  params: Promise<{
    handle: string;
  }>;
}

export default async function ProjectPage({ params }: ProjectPageProps) {
  const { handle } = await params;

  const queryClient = getQueryClient();
  await Promise.all([
    queryClient.prefetchQuery(getGetProjectByHandleQueryOptions(handle)),
    queryClient.prefetchInfiniteQuery(
      getGetReflectionsByProjectInfiniteQueryOptions(handle, { first: '10' }),
    ),
  ]);

  return (
    <HydrationBoundary state={dehydrate(queryClient)}>
      <div className="container max-w-3xl py-8 space-y-6">
        <ProjectOverview handle={handle} />
        <ProjectPosts handle={handle} />
      </div>
    </HydrationBoundary>
  );
}
