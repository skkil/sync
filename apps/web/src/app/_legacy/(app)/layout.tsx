import TopNavigationBar from '@/components/layout/nav/top/TopNavigationBar';

interface AppLayoutProps {
  children: React.ReactNode;
}

export default function AppLayout({ children }: AppLayoutProps) {
  return (
    <>
      <div>
        <TopNavigationBar />
      </div>

      <div className="pb-16 lg:pb-0">{children}</div>
    </>
  );
}
