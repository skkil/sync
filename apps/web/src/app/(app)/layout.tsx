import NavigationBar from '@/components/layout/nav/NavigationBar';

interface AppLayoutProps {
  children: React.ReactNode;
}

export default function AppLayout({ children }: AppLayoutProps) {
  return (
    <>
      <NavigationBar />
      {children}
    </>
  );
}
