'use client';

import { PencilSimpleIcon } from '@phosphor-icons/react';
import Link from 'next/link';

import { Button } from '@/components/ui/button';
import { Logo } from '@/components/ui/logo';

import NotificationsButton from './_components/NotificationsButton';
import ProjectSwitcher from './_components/ProjectSwitcher';
import SearchBar from './_components/SearchBar';
import UserAvatar from './_components/UserAvatar';

export default function TopNavigationBar() {
  return (
    <nav className="w-full flex items-center justify-between gap-3 bg-background px-4 md:px-8 py-3 border-b">
      <LeftSection />
      <RightSection />
    </nav>
  );
}

function LeftSection() {
  return (
    <div className="flex items-center gap-2 min-w-0">
      <Link href="/" className="shrink-0">
        <Logo />
      </Link>

      <ProjectSwitcher />

      <div className="hidden md:block">
        <SearchBar variant="desktop" />
      </div>
    </div>
  );
}

function RightSection() {
  return (
    <div className="flex items-center gap-1">
      <div className="md:hidden">
        <SearchBar variant="mobile" />
      </div>

      <Button asChild variant="ghost" size="icon" aria-label="새 포스트 작성">
        <Link href="/posts/new">
          <PencilSimpleIcon size={20} />
        </Link>
      </Button>

      <NotificationsButton />

      <UserAvatar />
    </div>
  );
}
