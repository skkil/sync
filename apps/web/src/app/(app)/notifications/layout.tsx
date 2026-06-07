import { BaseLayout } from '@/components/layout/app';
import LeftSidebar from '@/components/layout/sidebar/LeftSidebar';

interface NotificationsLayoutProps {
  children: React.ReactNode;
}

export default function NotificationsLayout({
  children,
}: NotificationsLayoutProps) {
  return <BaseLayout left={<LeftSidebar />}>{children}</BaseLayout>;
}
