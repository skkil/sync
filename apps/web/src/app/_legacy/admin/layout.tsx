import TopNavigationBar from '@/components/layout/nav/top/TopNavigationBar';

interface AdminLayoutProps {
  children: React.ReactNode;
}

export default function AdminLayout({ children }: AdminLayoutProps) {
  return (
    <>
      <TopNavigationBar />
      <div className="max-w-5xl mx-auto">
        <div>{children}</div>
      </div>
    </>
  );
}
