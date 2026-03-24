import { getTranslations } from 'next-intl/server';

export default async function Footer() {
  const t = await getTranslations('components.footer');

  return (
    <div className="flex flex-col">
      <div>
        <div>
          <span>{t('about')}</span> • <span>{t('contact')}</span> •{' '}
          <span>{t('terms')}</span> • <span>{t('privacy')}</span>
        </div>
        <Copyright />
      </div>
    </div>
  );
}

function Copyright() {
  return (
    <div>
      <span>SKKiL © 2026</span>
    </div>
  );
}
