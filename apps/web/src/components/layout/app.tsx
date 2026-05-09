import TopNavigationBar from '@/components/layout/nav/top/TopNavigationBar';

interface AppLayoutProps {
  left?: React.ReactNode;
  right?: React.ReactNode;
  children: React.ReactNode;
}

export function BaseLayout({ left, right, children }: AppLayoutProps) {
  return (
    <>
      <div>
        <TopNavigationBar />
      </div>

      <div className="pb-16 lg:pb-0">
        <div className="flex max-w-7xl px-4 mx-auto gap-5">
          <div className="hidden lg:block w-1/5">{left}</div>
          <div className="grow">{children}</div>
          <div className="hidden lg:block w-1/5">{right}</div>
        </div>
      </div>
    </>
  );
}
