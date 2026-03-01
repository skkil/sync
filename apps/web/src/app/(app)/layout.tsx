import NavigationBar from '@/components/layout/nav/NavigationBar';

interface AppLayoutProps {
  children: React.ReactNode;
}

export default function AppLayout({ children }: AppLayoutProps) {
  return (
    <>
      <div className="h-16">
        <NavigationBar />
      </div>
      <div className="h-[calc(100vh-4rem)]">{children}</div>
    </>
  );
}
