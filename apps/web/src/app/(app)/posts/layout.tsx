import { BaseLayout } from '@/components/layout/app';
import LeftSidebar from '@/components/layout/sidebar/LeftSidebar';

interface PostsLayoutProps {
  children: React.ReactNode;
}

export default function PostsLayout({ children }: PostsLayoutProps) {
  return <BaseLayout left={<LeftSidebar />}>{children}</BaseLayout>;
}
