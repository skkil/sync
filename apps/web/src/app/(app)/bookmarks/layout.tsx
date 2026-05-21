import { BaseLayout } from '@/components/layout/app';
import LeftSidebar from '@/components/layout/sidebar/LeftSidebar';

interface BookmarksLayoutProps {
  children: React.ReactNode;
}

export default function BookmarksLayout({ children }: BookmarksLayoutProps) {
  return <BaseLayout left={<LeftSidebar />}>{children}</BaseLayout>;
}
