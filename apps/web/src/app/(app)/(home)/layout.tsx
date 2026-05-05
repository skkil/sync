import { BaseLayout } from '@/components/layout/app';
import LeftSidebar from '@/components/layout/sidebar/LeftSidebar';

interface AppLayoutProps {
  children: React.ReactNode;
}

export default function AppLayout({ children }: AppLayoutProps) {
  return <BaseLayout left={<LeftSidebar />}>{children}</BaseLayout>;
}
