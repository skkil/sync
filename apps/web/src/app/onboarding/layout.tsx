import Logo from '@/components/ui/logo';

interface OnboardingLayoutProps {
  children?: React.ReactNode;
}

export default function OnboardingLayout({ children }: OnboardingLayoutProps) {
  return (
    <div className="flex min-h-screen items-center justify-center">
      <div className="absolute top-4 left-4">
        <Logo />
      </div>

      {children}
    </div>
  );
}
