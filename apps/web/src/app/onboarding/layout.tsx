import TopNavigationBar from '@/components/layout/nav/top/TopNavigationBar';

interface OnboardingLayoutProps {
  children?: React.ReactNode;
}

export default function OnboardingLayout({ children }: OnboardingLayoutProps) {
  return (
    <div className="flex min-h-screen items-center justify-center">
      <div className="absolute top-4 left-4">
        <TopNavigationBar />
      </div>

      {children}
    </div>
  );
}
