import {
  HydrationBoundary,
  QueryClient,
  dehydrate,
} from '@tanstack/react-query';

import { useGetProfileOptions } from '@/features/profile/api/get-profile';

import Profile from './_components/Profile';

interface ProfileProps {
  params: Promise<{
    id: string;
  }>;
}

export default async function ProfilePage({ params }: ProfileProps) {
  const { id } = await params;

  const queryClient = new QueryClient();
  await queryClient.prefetchQuery(useGetProfileOptions(id));

  return (
    <HydrationBoundary state={dehydrate(queryClient)}>
      <Profile userId={id} />
    </HydrationBoundary>
  );
}
