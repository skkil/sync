import { TripleColumnLayout } from '@/components/layout/columns';

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
}: ProjectSettingsLayoutProps) {
  return (
    <div>
      <TripleColumnLayout left={left} right={null} main={children} />
    </div>
  );
}
