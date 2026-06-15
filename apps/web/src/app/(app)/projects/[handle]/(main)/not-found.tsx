import { getTranslations } from 'next-intl/server';
import Link from 'next/link';

import { Button } from '@/components/ui/button';
import {
  Empty,
  EmptyContent,
  EmptyHeader,
  EmptyTitle,
} from '@/components/ui/empty';

export default async function ProjectNotFound() {
  const t = await getTranslations('pages.projects.project.not-found');

  return (
    <div className="flex w-full items-center justify-center">
      <Empty>
        <EmptyHeader>
          <EmptyTitle>{t('title')}</EmptyTitle>
        </EmptyHeader>
        <EmptyContent>
          <p className="text-muted-foreground">{t('description')}</p>
          <Link href="/">
            <Button variant="outline">{t('back')}</Button>
          </Link>
        </EmptyContent>
      </Empty>
    </div>
  );
}
