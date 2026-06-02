'use client';

import { useTranslations } from 'next-intl';
import Link from 'next/link';

import {
  Card,
  CardDescription,
  CardHeader,
  CardTitle,
} from '@/components/ui/card';

export default function AdminPage() {
  const t = useTranslations('pages.admin');

  return (
    <div className="space-y-6">
      <div>
        <h1 className="text-3xl font-bold tracking-tight">
          {t('dashboard.title')}
        </h1>
        <p className="text-muted-foreground">{t('dashboard.description')}</p>
      </div>

      <div className="gap-4">
        <Link href="/admin/tags">
          <Card className="cursor-pointer transition-colors">
            <CardHeader>
              <CardTitle>{t('tags.title')}</CardTitle>
              <CardDescription>{t('tags.description')}</CardDescription>
            </CardHeader>
          </Card>
        </Link>
      </div>
    </div>
  );
}
