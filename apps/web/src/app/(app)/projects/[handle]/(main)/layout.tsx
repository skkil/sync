import { TripleColumnLayout } from '@/components/layout/columns';

interface ProjectLayoutProps {
  children: React.ReactNode;
}

export default function ProjectLayout({ children }: ProjectLayoutProps) {
  return (
    <div>
      <TripleColumnLayout main={children} />
    </div>
  );
}
