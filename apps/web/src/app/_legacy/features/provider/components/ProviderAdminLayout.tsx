'use client';

import { EyeIcon } from '@phosphor-icons/react';
import { useTranslations } from 'next-intl';
import Link from 'next/link';
import { ReactNode } from 'react';

import { Button } from '@/components/ui/button';
import { Card } from '@/components/ui/card';
import { Tabs, TabsContent, TabsList, TabsTrigger } from '@/components/ui/tabs';

interface Tab {
  id: string;
  title: string;
  content: ReactNode;
}

interface ProviderAdminLayoutProps {
  tabs: Tab[];
  viewAsPath: string;
  children?: ReactNode;
}

export default function ProviderAdminLayout({
  tabs,
  viewAsPath,
  children,
}: ProviderAdminLayoutProps) {
  const t = useTranslations('pages.provider-admin');

  return (
    <div className="max-w-5xl mx-auto p-4 flex flex-col gap-5">
      {children}

      <div className="grow">
        <Tabs defaultValue={tabs[0]?.id} className="w-full">
          <div className="flex items-center justify-between">
            <TabsList>
              {tabs.map((tab) => (
                <TabsTrigger key={tab.id} value={tab.id}>
                  {tab.title}
                </TabsTrigger>
              ))}
            </TabsList>

            <Link href={viewAsPath}>
              <Button variant="outline">
                <EyeIcon />
                {t('view-as-member')}
              </Button>
            </Link>
          </div>

          <Card className="min-h-96 overflow-auto">
            {tabs.map((tab) => (
              <TabsContent key={tab.id} value={tab.id} className="px-5">
                {tab.content}
              </TabsContent>
            ))}
          </Card>
        </Tabs>
      </div>
    </div>
  );
}
