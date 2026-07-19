import { Metadata } from 'next';
import { getTranslations } from 'next-intl/server';

interface SearchLayoutProps {
  children: React.ReactNode;
}

export async function generateMetadata(): Promise<Metadata> {
  const t = await getTranslations('pages.search');

  return { title: t('title') };
}

export default function SearchLayout({ children }: SearchLayoutProps) {
  return children;
}
