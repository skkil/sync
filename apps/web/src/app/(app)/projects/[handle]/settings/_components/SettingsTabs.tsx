'use client';

import Link from 'next/link';
import { useParams, usePathname } from 'next/navigation';

import { Tabs, TabsList, TabsTrigger } from '@/components/ui/tabs';
import ROUTES from '@/util/routes';

interface SettingsTabsProps {
  children: React.ReactNode;
}

const TABS: {
  label: string;
  href: (handle: string) => string;
}[] = [
  { label: '워크스페이스', href: ROUTES.PROJECT_SETTINGS },
  { label: '팀원', href: ROUTES.PROJECT_SETTINGS_TEAMMATES },
];

export default function SettingsTabs({ children }: SettingsTabsProps) {
  const { handle } = useParams<{ handle: string }>();
  const pathname = usePathname();

  const activeTab = TABS.find((tab) => pathname === tab.href(handle))?.label;

  return (
    <div className="flex flex-col gap-8">
      <Tabs value={activeTab}>
        <TabsList variant="line" className="w-full">
          {TABS.map((tab) => (
            <TabsTrigger key={tab.label} value={tab.label} asChild>
              <Link href={tab.href(handle)}>{tab.label}</Link>
            </TabsTrigger>
          ))}
        </TabsList>
      </Tabs>

      {children}
    </div>
  );
}
