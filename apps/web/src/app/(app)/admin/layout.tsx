import { BaseLayout } from '@/components/layout/app';

interface AdminLayoutProps {
  children: React.ReactNode;
}

export default function AdminLayout({ children }: AdminLayoutProps) {
  return <BaseLayout>{children}</BaseLayout>;
}
