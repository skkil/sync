import { HydrationBoundary, dehydrate } from '@tanstack/react-query';
import { Metadata } from 'next';
import { getTranslations } from 'next-intl/server';
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

export async function generateMetadata(): Promise<Metadata> {
  const t = await getTranslations('pages.projects.project.my-comments');

  return { title: t('heading') };
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
