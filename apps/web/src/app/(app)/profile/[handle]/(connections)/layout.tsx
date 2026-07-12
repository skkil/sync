import { HydrationBoundary, dehydrate } from '@tanstack/react-query';

import { getGetProfileByHandleQueryOptions } from '@/api/__generated__/profile/profile';
import { TwoColumnLayout } from '@/components/layout/TwoColumnLayout';
import { getQueryClient } from '@/lib/query';

import ProfileConnectionsHeader from '../_components/ProfileConnectionsHeader';
import ProfileConnectionsTabs from '../_components/ProfileConnectionsTabs';

interface ProfileConnectionsLayoutProps {
  children: React.ReactNode;
  params: Promise<{ handle: string }>;
}

export default async function ProfileConnectionsLayout({
  children,
  params,
}: ProfileConnectionsLayoutProps) {
  const { handle } = await params;
  const queryClient = getQueryClient();
  await queryClient.prefetchQuery(getGetProfileByHandleQueryOptions(handle));

  return (
    <HydrationBoundary state={dehydrate(queryClient)}>
      <TwoColumnLayout
        main={
          <div className="space-y-4">
            <ProfileConnectionsHeader handle={handle} />
            <ProfileConnectionsTabs handle={handle}>
              {children}
            </ProfileConnectionsTabs>
          </div>
        }
        side={undefined}
        hideSideOnMobile
      />
    </HydrationBoundary>
  );
}
