import { BaseLayout } from '@/components/layout/app';

interface AppLayoutProps {
  children: React.ReactNode;
}

export default function AppLayout({ children }: AppLayoutProps) {
  return <BaseLayout>{children}</BaseLayout>;
}
