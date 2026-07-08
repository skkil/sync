import { getTranslations } from 'next-intl/server';

import { NotFound } from '@/components/ui/state';

export default async function ProjectSettingsNotFound() {
  const t = await getTranslations('pages.projects.project.not-found');

  return (
    <NotFound
      title={t('title')}
      description={t('description')}
      backLabel={t('back')}
    />
  );
}
