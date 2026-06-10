'use client';

import Link from 'next/link';
import { usePathname, useSearchParams } from 'next/navigation';

import { Card } from '@/components/ui/card';
import { Tabs, TabsContent, TabsList, TabsTrigger } from '@/components/ui/tabs';

export interface Tab {
  id: string;
  title: string;
  href: string;
  startsWith?: boolean;
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
    tabs.find(
      (tab) =>
        pathname === tab.href ||
        (tab.startsWith && pathname.startsWith(tab.href)),
    )?.id ?? tabs[0]?.id;

  return (
    <Tabs value={activeTab} className="w-full">
      <div className="flex items-center justify-between mb-4">
        <TabsList>
          {tabs.map((tab) => (
            <TabsTrigger key={tab.id} value={tab.id} asChild>
              <Link href={`${tab.href}?${searchParams.toString()}`}>
                {tab.title}
              </Link>
            </TabsTrigger>
          ))}
        </TabsList>

        {right && <div className="ml-auto">{right}</div>}
      </div>

      <Card className="min-h-96 overflow-auto">
        {tabs.map((tab) =>
          tab.id === activeTab ? (
            <TabsContent key={tab.id} value={tab.id} className="px-5">
              {children}
            </TabsContent>
          ) : null,
        )}
      </Card>
    </Tabs>
  );
}
