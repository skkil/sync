import { notFound } from 'next/navigation';

import { getProjectByHandle } from '@/api/__generated__/project/project';
import { TwoColumnLayout } from '@/components/layout/TwoColumnLayout';
import SyncError, { ErrorCode } from '@/lib/error';

import ProjectDashboard from './_components/ProjectDashboard';
import ProjectHeader from './_components/ProjectHeader';
import ProjectInfoSidebar from './_components/ProjectInfoSidebar';

interface ProjectDashboardPageProps {
  params: Promise<{
    handle: string;
  }>;
}

export default async function ProjectDashboardPage({
  params,
}: ProjectDashboardPageProps) {
  const { handle } = await params;

  try {
    await getProjectByHandle(handle);
  } catch (error) {
    if (error instanceof SyncError) {
      switch (error.code) {
        case ErrorCode.PROJECT_NOT_FOUND:
          notFound();
      }
    }
  }

  return (
    <div className="space-y-6">
      <ProjectHeader handle={handle} />

      <TwoColumnLayout
        main={<ProjectDashboard handle={handle} />}
        side={<ProjectInfoSidebar handle={handle} />}
        reverseSideOnMobile
      />
    </div>
  );
}
