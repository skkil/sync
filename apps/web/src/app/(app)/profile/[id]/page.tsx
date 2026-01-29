import { HydrationBoundary, dehydrate } from '@tanstack/react-query';

import { useGetProfileOptions } from '@/features/profile/api/get-profile';
import { getQueryClient } from '@/lib/query';

import Profile from './_components/Profile';

interface ProfileProps {
  params: Promise<{
    id: string;
  }>;
}

export default async function ProfilePage({ params }: ProfileProps) {
  const { id } = await params;

  const queryClient = getQueryClient();
  await queryClient.prefetchQuery(useGetProfileOptions(id));

  return (
    <HydrationBoundary state={dehydrate(queryClient)}>
      <Profile userId={id} />
    </HydrationBoundary>
  );
}
