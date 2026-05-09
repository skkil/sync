import { HydrationBoundary, dehydrate } from '@tanstack/react-query';

import { getGetProfileByHandleQueryOptions } from '@/api/__generated__/profile/profile';
import { getQueryClient } from '@/lib/query';

import ProfileCard from './_components/ProfileCard';
import Streaks from './_components/Streaks';

interface ProfileProps {
  params: Promise<{
    handle: string;
  }>;
}

export default async function Profile({ params }: ProfileProps) {
  const { handle } = await params;

  const queryClient = getQueryClient();
  await queryClient.prefetchQuery(getGetProfileByHandleQueryOptions(handle));

  return (
    <HydrationBoundary state={dehydrate(queryClient)}>
      <div className="space-y-4">
        <ProfileCard handle={handle} />
        <Streaks handle={handle} />
      </div>
    </HydrationBoundary>
  );
}
