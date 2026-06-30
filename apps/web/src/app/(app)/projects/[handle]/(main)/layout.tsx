import { TripleColumnLayout } from '@/components/layout/columns';
import { Separator } from '@/components/ui/separator';

import LeftSidebar from './_components/LeftSidebar';
import ProjectContext from './_components/ProjectContext';
import ProjectHeader from './_components/ProjectHeader';
import RightSidebar from './_components/RightSidebar';

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
      <ProjectHeader handle={handle} />

      <Separator className="my-5" />

      <TripleColumnLayout
        left={<LeftSidebar handle={handle} />}
        right={<RightSidebar handle={handle} />}
        main={children}
      />
    </div>
  );
}
