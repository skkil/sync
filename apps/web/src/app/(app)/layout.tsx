import TopNavigationBar from '@/components/layout/nav/top/TopNavigationBar';
import LeftSidebar from '@/components/layout/sidebar/LeftSidebar';

interface AppLayoutProps {
  children: React.ReactNode;
}

export default function AppLayout({ children }: AppLayoutProps) {
  return (
    <>
      <div>
        <TopNavigationBar />
      </div>

      <div className="pb-16 lg:pb-0">
        <div className="flex max-w-7xl px-4 mx-auto gap-5">
          <div className="hidden lg:block w-1/5">
            <LeftSidebar />
          </div>
          <div className="grow">{children}</div>
          <div className="hidden lg:block w-1/5"></div>
        </div>
      </div>
    </>
  );
}
