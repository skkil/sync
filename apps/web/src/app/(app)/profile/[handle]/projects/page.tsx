import { HydrationBoundary, dehydrate } from '@tanstack/react-query';

import { getGetProjectsByUserQueryOptions } from '@/api/__generated__/project/project';
import { getQueryClient } from '@/lib/query';

import ProfileProjectsList from './_components/ProfileProjectsList';

interface ProfileProjectsProps {
  params: Promise<{
    handle: string;
  }>;
}

export default async function ProfileProjects({
  params,
}: ProfileProjectsProps) {
  const { handle } = await params;

  const queryClient = getQueryClient();
  await queryClient.prefetchQuery(getGetProjectsByUserQueryOptions(handle));

  return (
    <HydrationBoundary state={dehydrate(queryClient)}>
      <div className="space-y-4">
        <ProfileProjectsList handle={handle} />
      </div>
    </HydrationBoundary>
  );
}
