import TopNavigationBar from '@/components/layout/navbar/TopNavigationBar';

interface AuthLayoutProps {
  children?: React.ReactNode;
}

export default function AuthLayout({ children }: AuthLayoutProps) {
  return (
    <div className="w-full min-h-screen flex flex-col">
      <div>
        <TopNavigationBar showSidebarTrigger={false} />
      </div>

      <div className="grow flex items-center justify-center">{children}</div>
    </div>
  );
}
