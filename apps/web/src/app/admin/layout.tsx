import NavigationBar from '@/components/layout/nav/NavigationBar';

interface AdminLayoutProps {
  children: React.ReactNode;
}

export default function AdminLayout({ children }: AdminLayoutProps) {
  return (
    <>
      <NavigationBar />
      <div className="max-w-5xl mx-auto">
        <div>{children}</div>
      </div>
    </>
  );
}
