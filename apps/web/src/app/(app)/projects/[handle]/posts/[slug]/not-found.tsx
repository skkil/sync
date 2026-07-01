import { getTranslations } from 'next-intl/server';

import { NotFound } from '@/components/ui/state';

export default async function PostNotFound() {
  const t = await getTranslations('pages.posts.not-found');

  return (
    <NotFound
      title={t('title')}
      description={t('description')}
      backLabel={t('back')}
    />
  );
}
