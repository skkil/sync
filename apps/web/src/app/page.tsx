import { getTranslations } from 'next-intl/server';

import { Button } from '@/components/ui/button';

export default async function Home() {
  const t = await getTranslations('pages.home');

  return (
    <div className="min-h-screen flex flex-col items-center justify-center gap-6">
      <Button>{t('title')}</Button>
    </div>
  );
}
