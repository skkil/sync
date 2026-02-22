import { getTranslations } from 'next-intl/server';

import {
  Empty,
  EmptyContent,
  EmptyHeader,
  EmptyTitle,
} from '@/components/ui/empty';

export default async function NotFound() {
  const t = await getTranslations('pages.not-found');

  return (
    <div className="flex w-full items-center justify-center">
      <Empty>
        <EmptyHeader>
          <EmptyTitle>{t('title')}</EmptyTitle>
        </EmptyHeader>
        <EmptyContent></EmptyContent>
      </Empty>
    </div>
  );
}
