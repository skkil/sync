import TopNavigationBar from '@/components/layout/nav/top/TopNavigationBar';

interface AppLayoutProps {
  children: React.ReactNode;
}

export default function AppLayout({ children }: AppLayoutProps) {
  return (
    <div className="min-h-screen flex flex-col">
      <TopNavigationBar />
      <div className="grow w-full max-w-7xl px-7 pt-7 mx-auto">
        <div className="h-full w-full">{children}</div>
      </div>
    </div>
  );
}
