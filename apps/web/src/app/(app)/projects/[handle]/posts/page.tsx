import { HydrationBoundary, dehydrate } from '@tanstack/react-query';
import { Metadata } from 'next';
import { getTranslations } from 'next-intl/server';
import { notFound } from 'next/navigation';

import { getGetProjectByHandleQueryOptions } from '@/api/__generated__/project/project';
import SyncError, { ErrorCode } from '@/lib/error';
import { getQueryClient } from '@/lib/query';

import ProjectPosts from './_components/ProjectPosts';

interface ProjectPostsPageProps {
  params: Promise<{
    handle: string;
  }>;
  searchParams: Promise<{
    type?: string;
    authorHandle?: string;
  }>;
}

export async function generateMetadata(): Promise<Metadata> {
  const t = await getTranslations('pages.projects.project.posts');

  return { title: t('label') };
}

export default async function ProjectPostsPage({
  params,
  searchParams,
}: ProjectPostsPageProps) {
  const { handle } = await params;
  const { type, authorHandle } = await searchParams;

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
      <ProjectPosts handle={handle} type={type} authorHandle={authorHandle} />
    </HydrationBoundary>
  );
}
