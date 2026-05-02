import { getTranslations } from 'next-intl/server';

interface TeamBuildingLayoutProps {
  children: React.ReactNode;
}

export default async function TeamBuildingLayout({
  children,
}: TeamBuildingLayoutProps) {
  const t = await getTranslations('pages.team-building');
  return (
    <div>
      <div className="hidden lg:block mb-6">
        <h1 className="text-xl font-semibold">{t('title')}</h1>
        <p className="text-sm">{t('description')}</p>
      </div>

      <div>{children}</div>
    </div>
  );
}
