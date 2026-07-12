'use client';

import { useTranslations } from 'next-intl';
import Link from 'next/link';
import { usePathname } from 'next/navigation';

import { Tabs, TabsList, TabsTrigger } from '@/components/ui/tabs';
import ROUTES from '@/util/routes';

interface ProfileConnectionsTabsProps {
  handle: string;
  children: React.ReactNode;
}

export default function ProfileConnectionsTabs({
  handle,
  children,
}: ProfileConnectionsTabsProps) {
  const t = useTranslations('pages.profile.connections');
  const pathname = usePathname();

  const tabs = [
    { value: 'followers', href: ROUTES.PROFILE_FOLLOWERS(handle) },
    { value: 'following', href: ROUTES.PROFILE_FOLLOWING(handle) },
  ] as const;

  const activeTab = tabs.find((tab) => pathname === tab.href)?.value;

  return (
    <div className="flex flex-col gap-4">
      <Tabs value={activeTab}>
        <TabsList variant="line">
          {tabs.map((tab) => (
            <TabsTrigger key={tab.value} value={tab.value} asChild>
              <Link href={tab.href}>{t(`tabs.${tab.value}`)}</Link>
            </TabsTrigger>
          ))}
        </TabsList>
      </Tabs>

      {children}
    </div>
  );
}
