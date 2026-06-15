import { TripleColumnLayout } from '@/components/layout/columns';

interface AppLayoutProps {
  children: React.ReactNode;
}

export default function AppLayout({ children }: AppLayoutProps) {
  return (
    <TripleColumnLayout main={children} left={undefined} right={undefined} />
  );
}
