'use client';

import { useTranslations } from 'next-intl';
import Link from 'next/link';

import { Button } from '@/components/ui/button';
import { Logo } from '@/components/ui/logo';
import { SidebarTrigger } from '@/components/ui/sidebar';
import { isAuthenticated } from '@/lib/auth';
import { useSession } from '@/lib/auth/client';

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
      <Link href="/">
        <Logo />
      </Link>
      <div className="hidden md:block">
        <SearchBar variant="desktop" />
      </div>
      <div className="md:hidden">
        <SearchBar variant="mobile" />
      </div>
    </div>
  );
}

function RightSection() {
  const t = useTranslations('components.navigation');

  const { data: session, isPending } = useSession();

  if (isPending) {
    return <div className="flex items-center gap-1" />;
  }

  return (
    <div className="flex items-center gap-1">
      {isAuthenticated(session) && <NotificationsButton />}

      {isAuthenticated(session) ? (
        <UserAvatar />
      ) : (
        <Link href="/auth/login">
          <Button variant="ghost">{t('login')}</Button>
        </Link>
      )}
    </div>
  );
}
