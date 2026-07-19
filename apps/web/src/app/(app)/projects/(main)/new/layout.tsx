import { Metadata } from 'next';
import { getTranslations } from 'next-intl/server';

interface NewProjectLayoutProps {
  children: React.ReactNode;
}

export async function generateMetadata(): Promise<Metadata> {
  const t = await getTranslations('pages.projects.new');

  return { title: t('metaTitle') };
}

export default function NewProjectLayout({ children }: NewProjectLayoutProps) {
  return children;
}
