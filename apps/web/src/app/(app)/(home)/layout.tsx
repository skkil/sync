import { TripleColumnLayout } from '@/components/layout/columns';
import LeftSidebar from '@/components/layout/sidebar/LeftSidebar';

interface HomeLayoutProps {
  children: React.ReactNode;
}

export default function AppLayout({ children }: HomeLayoutProps) {
  return (
    <TripleColumnLayout left={<LeftSidebar />} right={null} main={children} />
  );
}
