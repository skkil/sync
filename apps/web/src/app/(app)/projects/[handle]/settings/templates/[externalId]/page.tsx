import { HydrationBoundary, dehydrate } from '@tanstack/react-query';
import { Metadata } from 'next';
import { getTranslations } from 'next-intl/server';
import { notFound } from 'next/navigation';

import { getGetProjectByHandleQueryOptions } from '@/api/__generated__/project/project';
import SyncError, { ErrorCode } from '@/lib/error';
import { getQueryClient } from '@/lib/query';

import TemplateEditorView from '../_components/TemplateEditorView';

interface EditTemplatePageProps {
  params: Promise<{
    handle: string;
    externalId: string;
  }>;
}

export async function generateMetadata(): Promise<Metadata> {
  const t = await getTranslations('pages.projects.project.settings.templates');

  return { title: t('form.heading-edit') };
}

export default async function EditTemplatePage({
  params,
}: EditTemplatePageProps) {
  const { handle, externalId } = await params;

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
      <TemplateEditorView externalId={decodeURIComponent(externalId)} />
    </HydrationBoundary>
  );
}
