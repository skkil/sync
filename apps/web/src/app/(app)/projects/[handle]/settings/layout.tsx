import { ArrowLeftIcon } from '@phosphor-icons/react/dist/ssr';
import Link from 'next/link';

import { TripleColumnLayout } from '@/components/layout/columns';
import { Separator } from '@/components/ui/separator';

interface ProjectSettingsLayoutProps {
  children: React.ReactNode;
  left: React.ReactNode;
  params: Promise<{
    handle: string;
  }>;
}

export default async function ProjectSettingsLayout({
  children,
  left,
  params,
}: ProjectSettingsLayoutProps) {
  const { handle } = await params;

  return (
    <div>
      <Link className="flex items-center gap-2" href={`/projects/${handle}`}>
        <ArrowLeftIcon />
        <span>Back to Project</span>
      </Link>
      <Separator className="my-4" />
      <TripleColumnLayout left={left} right={null} main={children} />
    </div>
  );
}
