'use client';

import {
  BookmarkSimpleIcon,
  ChatCircleDotsIcon,
  ProjectorScreenChartIcon,
} from '@phosphor-icons/react';
import { useTranslations } from 'next-intl';
import Link from 'next/link';
import { usePathname } from 'next/navigation';

import { Button } from '@/components/ui/button';
import { Logo } from '@/components/ui/logo';
import { useSession } from '@/lib/auth/client';
import { cn } from '@/lib/utils';

import MobileNavigationMenu from './_components/MobileNavigationMenu';
import SearchBar from './_components/SearchBar';
import UserAvatar from './_components/UserAvatar';

export default function TopNavigationBar() {
  const path = usePathname();
  const { data: session } = useSession();

  return (
    <>
      <div className="hidden lg:block">
        <TopNavigationBarDesktop
          path={path}
          isAuthenticated={!!session && !!session.user}
        />
      </div>

      <div className="block lg:hidden">
        <TopNavigationBarMobile />
      </div>
    </>
  );
}

function TopNavigationBarDesktop({
  path,
  isAuthenticated,
}: {
  path: string;
  isAuthenticated: boolean;
}) {
  const t = useTranslations('components.navigation');

  const tabs = [
    {
      id: 'bookmarks',
      href: '/bookmarks',
      icon: <BookmarkSimpleIcon />,
      highlight: () => {
        return path.startsWith('/bookmarks');
      },
      authenticated: true,
    },
    {
      id: 'projects',
      href: '/projects',
      icon: <ProjectorScreenChartIcon />,
      highlight: () => {
        return path.startsWith('/projects');
      },
    },
    {
      id: 'messages',
      href: '/messages',
      icon: <ChatCircleDotsIcon />,
      highlight: () => {
        return path.startsWith('/messages');
      },
      authenticated: true,
    },
  ];

  return (
    <nav className="w-full flex items-center justify-between gap-2 bg-background p-3 px-4 md:px-8 border-b mb-4">
      <Link href="/">
        <Logo />
      </Link>

      <div className="flex items-center justify-end grow gap-4">
        <SearchBar />

        {tabs.map((tab) => {
          if (tab.authenticated && !isAuthenticated) {
            return null;
          }

          return (
            <Link
              key={tab.id}
              href={tab.href}
              className={cn(
                'flex flex-col items-center justify-center',
                tab.highlight() ? '' : 'text-foreground/80',
              )}
            >
              <Button variant="ghost" size="icon">
                {tab.icon}
              </Button>

              <span className="text-xs">{t(`menu.${tab.id}`)}</span>
            </Link>
          );
        })}

        <UserAvatar />
      </div>
    </nav>
  );
}

function TopNavigationBarMobile() {
  return (
    <nav className="h-16 w-full flex items-center justify-between px-8">
      <div className="flex items-center mr-4">
        <Logo />
      </div>

      <div className="grow flex items-center gap-2">
        <SearchBar />
        <MobileNavigationMenu />
      </div>
    </nav>
  );
}
