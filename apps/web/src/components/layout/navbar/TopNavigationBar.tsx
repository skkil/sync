'use client';

import { useTranslations } from 'next-intl';
import Link from 'next/link';

import { Button } from '@/components/ui/button';
import { Logo } from '@/components/ui/logo';
import { SidebarTrigger } from '@/components/ui/sidebar';
import { useMounted } from '@/hooks/use-mounted';
import { isAuthenticated } from '@/lib/auth';
import { useSession } from '@/lib/auth/client';
import ROUTES from '@/util/routes';

import NotificationsButton from './_components/NotificationsButton';
import SearchBar from './_components/SearchBar';
import UserAvatar from './_components/UserAvatar';

interface TopNavigationBarProps {
  showSidebarTrigger?: boolean;
}

export default function TopNavigationBar({
  showSidebarTrigger = true,
}: TopNavigationBarProps) {
  return (
    <nav className="w-full flex items-center justify-between gap-3 bg-background px-4 py-3 border-b">
      <LeftSection showSidebarTrigger={showSidebarTrigger} />
      <RightSection />
    </nav>
  );
}

function LeftSection({ showSidebarTrigger }: { showSidebarTrigger: boolean }) {
  return (
    <div className="flex items-center gap-2 min-w-0">
      {showSidebarTrigger && <SidebarTrigger className="md:hidden" />}
      <Link href={ROUTES.HOME()}>
        <Logo />
      </Link>
    </div>
  );
}

function RightSection() {
  const t = useTranslations('components.navigation');

  // `useSession` can resolve synchronously from its client-side cache before
  // hydration, while SSR always renders the pending state. Gating on
  // `mounted` keeps the first client render identical to the server-rendered
  // HTML so this subtree doesn't diverge and trigger a hydration mismatch.
  const mounted = useMounted();

  const { data: session, isPending } = useSession();

  if (!mounted || isPending) {
    return <div className="flex items-center gap-1" />;
  }

  return (
    <div className="flex items-center gap-1">
      <>
        <div className="hidden md:block">
          <SearchBar variant="desktop" />
        </div>
        <div className="md:hidden">
          <SearchBar variant="mobile" />
        </div>
      </>

      {isAuthenticated(session) && <NotificationsButton />}

      {isAuthenticated(session) ? (
        <UserAvatar />
      ) : (
        <Link href={ROUTES.LOGIN()}>
          <Button variant="ghost">{t('login')}</Button>
        </Link>
      )}
    </div>
  );
}
