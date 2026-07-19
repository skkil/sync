import { HydrationBoundary, dehydrate } from '@tanstack/react-query';
import { Metadata } from 'next';
import { getTranslations } from 'next-intl/server';

import { getGetProjectsByUserQueryOptions } from '@/api/__generated__/project/project';
import { getQueryClient } from '@/lib/query';

import ProfileProjectsList from './_components/ProfileProjectsList';

interface ProfileProjectsProps {
  params: Promise<{
    handle: string;
  }>;
}

export async function generateMetadata(): Promise<Metadata> {
  const t = await getTranslations('pages.profile.projects');

  return { title: t('label') };
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
