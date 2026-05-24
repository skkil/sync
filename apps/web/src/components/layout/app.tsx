import TopNavigationBar from '@/components/layout/nav/top/TopNavigationBar';

interface AppLayoutProps {
  left?: React.ReactNode;
  right?: React.ReactNode;
  children: React.ReactNode;
}

export function BaseLayout({ left, right, children }: AppLayoutProps) {
  return (
    <div className="min-h-screen flex flex-col">
      <div>
        <TopNavigationBar />
      </div>

      <div className="grow flex pb-16 lg:pb-0 px-4 flex gap-5">
        <div className="hidden lg:block w-1/5">{left}</div>
        <div className="grow">{children}</div>
        <div className="hidden lg:block w-1/5">{right}</div>
      </div>
    </div>
  );
}
