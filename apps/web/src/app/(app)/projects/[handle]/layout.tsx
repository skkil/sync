import { Metadata } from 'next';

import { getProjectByHandle } from '@/api/__generated__/project/project';

interface ProjectLayoutProps {
  children: React.ReactNode;
  params: Promise<{
    handle: string;
  }>;
}

export async function generateMetadata({
  params,
}: ProjectLayoutProps): Promise<Metadata> {
  const { handle } = await params;

  try {
    const { data: project } = await getProjectByHandle(handle);

    return { title: project.name };
  } catch {
    return {};
  }
}

export default function ProjectLayout({ children }: ProjectLayoutProps) {
  return children;
}
