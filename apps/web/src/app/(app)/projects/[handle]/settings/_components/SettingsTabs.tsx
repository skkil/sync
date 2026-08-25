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
      id: 'project',
      label: t('project'),
      href: ROUTES.PROJECT_SETTINGS,
    },
    {
      id: 'teammates',
      label: t('teammates'),
      href: ROUTES.PROJECT_SETTINGS_TEAMMATES,
    },
    {
      id: 'templates',
      label: t('templates'),
      href: ROUTES.PROJECT_SETTINGS_TEMPLATES,
    },
  ];

  // 템플릿 작성처럼 탭 경로 아래의 하위 라우트에서도 해당 탭이 활성으로 보이도록,
  // 정확 일치가 아니라 가장 긴 프리픽스로 고른다.
  const activeTab = TABS.filter((tab) => {
    const href = tab.href(handle);
    return pathname === href || pathname.startsWith(href + '/');
  }).sort((a, b) => b.href(handle).length - a.href(handle).length)[0]?.id;

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
