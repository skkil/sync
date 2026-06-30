import { TripleColumnLayout } from '@/components/layout/columns';
import LeftSidebar from '@/components/layout/sidebar/LeftSidebar';

interface BookmarksLayoutProps {
  children: React.ReactNode;
}

export default function BookmarksLayout({ children }: BookmarksLayoutProps) {
  return (
    <TripleColumnLayout
      left={<LeftSidebar />}
      main={children}
      right={undefined}
    />
  );
}
