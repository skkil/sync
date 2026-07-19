import { HydrationBoundary, dehydrate } from '@tanstack/react-query';
import { Metadata } from 'next';
import { getTranslations } from 'next-intl/server';
import { notFound } from 'next/navigation';

import { getGetProjectByHandleQueryOptions } from '@/api/__generated__/project/project';
import { requireOnboardedSession } from '@/lib/auth/guards';
import SyncError, { ErrorCode } from '@/lib/error';
import { getQueryClient } from '@/lib/query';

import ProjectPosts from '../_components/ProjectPosts';

interface ProjectMyPostsPageProps {
  params: Promise<{
    handle: string;
  }>;
}

export async function generateMetadata(): Promise<Metadata> {
  const t = await getTranslations('components.layout.sidebar.nav');

  return { title: t('my-posts') };
}

export default async function ProjectMyPostsPage({
  params,
}: ProjectMyPostsPageProps) {
  const { handle } = await params;
  const session = await requireOnboardedSession();

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
      <ProjectPosts handle={handle} authorHandle={session.user.handle} />
    </HydrationBoundary>
  );
}
