'use client';

import Link from 'next/link';
import { usePathname, useSearchParams } from 'next/navigation';

import { Card } from '@/components/ui/card';
import { Tabs, TabsContent, TabsList, TabsTrigger } from '@/components/ui/tabs';

interface Tab {
  id: string;
  title: string;
  href: string;
}

interface ProviderTabsProps {
  tabs: Tab[];
  right?: React.ReactNode;
  children: React.ReactNode;
}

export function ProviderTabs({ tabs, right, children }: ProviderTabsProps) {
  const pathname = usePathname();
  const searchParams = useSearchParams();

  const activeTab =
    tabs.find((tab) => pathname === tab.href)?.id ?? tabs[0]?.id;

  return (
    <Tabs value={activeTab} className="w-full">
      <div className="flex items-center justify-between mb-4">
        <TabsList>
          {tabs.map((tab) => (
            <Link key={tab.id} href={`${tab.href}?${searchParams.toString()}`}>
              <TabsTrigger value={tab.id}>{tab.title}</TabsTrigger>
            </Link>
          ))}
        </TabsList>

        {right && <div className="ml-auto">{right}</div>}
      </div>

      <Card className="min-h-96 overflow-auto">
        {tabs.map((tab) => (
          <TabsContent key={tab.id} value={tab.id} className="px-5">
            {children}
          </TabsContent>
        ))}
      </Card>
    </Tabs>
  );
}
