import Navigation from '@/components/layout/Navigation';

interface AppLayoutProps {
  children: React.ReactNode;
}

export default function AppLayout({ children }: AppLayoutProps) {
  return (
    <>
      <Navigation />
      {children}
    </>
  );
}
