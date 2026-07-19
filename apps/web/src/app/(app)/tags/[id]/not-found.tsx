import { getTranslations } from 'next-intl/server';

import { NotFound } from '@/components/ui/state';

export default async function TagNotFound() {
  const t = await getTranslations('components.tag.detail.not-found');

  return (
    <NotFound
      title={t('title')}
      description={t('description')}
      backLabel={t('back')}
    />
  );
}
