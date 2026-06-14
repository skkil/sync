import { TripleColumnLayout } from '@/components/layout/columns';
import LeftSidebar from '@/components/layout/sidebar/LeftSidebar';

interface PostsLayoutProps {
  children: React.ReactNode;
}

export default function PostsLayout({ children }: PostsLayoutProps) {
  return (
    <TripleColumnLayout
      left={<LeftSidebar />}
      main={children}
      right={undefined}
    />
  );
}
