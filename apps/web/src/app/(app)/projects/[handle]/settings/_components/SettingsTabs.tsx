'use client';

import { useTranslations } from 'next-intl';
import Link from 'next/link';
import { useParams, usePathname } from 'next/navigation';

import { Tabs, TabsList, TabsTrigger } from '@/components/ui/tabs';
import ROUTES from '@/util/routes';

interface SettingsTabsProps {
  children: React.ReactNode;
}

export default function SettingsTabs({ children }: SettingsTabsProps) {
  const t = useTranslations('pages.projects.project.settings.tabs');

  const { handle } = useParams<{ handle: string }>();
  const pathname = usePathname();

  const TABS: {
    id: string;
    label: string;
    href: (handle: string) => string;
  }[] = [
    {
      id: 'workspace',
      label: t('workspace'),
      href: ROUTES.PROJECT_SETTINGS,
    },
    {
      id: 'teammates',
      label: t('teammates'),
      href: ROUTES.PROJECT_SETTINGS_TEAMMATES,
    },
  ];

  const activeTab = TABS.find((tab) => pathname === tab.href(handle))?.id;

  return (
    <div className="flex flex-col gap-8">
      <Tabs value={activeTab}>
        <TabsList variant="line" className="w-full">
          {TABS.map((tab) => (
            <TabsTrigger key={tab.id} value={tab.id} asChild>
              <Link href={tab.href(handle)}>{tab.label}</Link>
            </TabsTrigger>
          ))}
        </TabsList>
      </Tabs>

      {children}
    </div>
  );
}
