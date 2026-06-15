'use client';

import Link from 'next/link';
import { useParams, usePathname } from 'next/navigation';

import { cn } from '@/lib/utils';

const TABS = [{ label: 'Teammates', href: 'teammates' }];

export default function SettingsSidebar() {
  const { handle } = useParams<{ handle: string }>();
  const pathname = usePathname();

  return (
    <nav className="flex flex-col gap-1">
      {TABS.map((tab) => {
        const href = `/projects/${handle}/settings/${tab.href}`;
        const isActive = pathname === href;

        return (
          <Link
            key={tab.href}
            href={href}
            className={cn(
              'px-3 py-2 rounded-md text-sm transition-colors',
              isActive
                ? 'bg-accent text-accent-foreground font-medium'
                : 'text-muted-foreground hover:text-foreground hover:bg-accent/50',
            )}
          >
            {tab.label}
          </Link>
        );
      })}
    </nav>
  );
}
