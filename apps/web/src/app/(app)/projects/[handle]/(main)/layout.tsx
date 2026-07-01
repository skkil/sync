import { TripleColumnLayout } from '@/components/layout/columns';

import ProjectContext from './_components/ProjectContext';

interface ProjectLayoutProps {
  children: React.ReactNode;
  params: Promise<{
    handle: string;
  }>;
}

export default async function ProjectLayout({
  children,
  params,
}: ProjectLayoutProps) {
  const { handle } = await params;

  return (
    <div>
      <ProjectContext handle={handle} />

      <TripleColumnLayout main={children} />
    </div>
  );
}
