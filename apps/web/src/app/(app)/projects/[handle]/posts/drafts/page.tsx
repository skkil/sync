import { HydrationBoundary, dehydrate } from '@tanstack/react-query';
import { Metadata } from 'next';
import { getTranslations } from 'next-intl/server';
import { notFound } from 'next/navigation';

import { getGetProjectByHandleQueryOptions } from '@/api/__generated__/project/project';
import { requireOnboardedSession } from '@/lib/auth/guards';
import SyncError, { ErrorCode } from '@/lib/error';
import { getQueryClient } from '@/lib/query';

import ProjectDrafts from '../_components/ProjectDrafts';

interface ProjectDraftsPageProps {
  params: Promise<{
    handle: string;
  }>;
}

export async function generateMetadata(): Promise<Metadata> {
  const t = await getTranslations('pages.projects.project.drafts');

  return { title: t('heading') };
}

export default async function ProjectDraftsPage({
  params,
}: ProjectDraftsPageProps) {
  const { handle } = await params;
  await requireOnboardedSession();

  const queryClient = getQueryClient();
  let role: string | null | undefined;

  try {
    const project = await queryClient.fetchQuery(
      getGetProjectByHandleQueryOptions(handle),
    );
    role = project.data.role;
  } catch (error) {
    if (error instanceof SyncError) {
      switch (error.code) {
        case ErrorCode.PROJECT_NOT_FOUND:
          notFound();
      }
    }
  }

  if (!role) {
    notFound();
  }

  return (
    <HydrationBoundary state={dehydrate(queryClient)}>
      <ProjectDrafts handle={handle} />
    </HydrationBoundary>
  );
}
