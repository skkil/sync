import { Metadata } from 'next';
import { getTranslations } from 'next-intl/server';

interface CreateProjectPostLayoutProps {
  children: React.ReactNode;
}

export async function generateMetadata(): Promise<Metadata> {
  const t = await getTranslations('pages.posts.new');

  return { title: t('title') };
}

export default function CreateProjectPostLayout({
  children,
}: CreateProjectPostLayoutProps) {
  return children;
}
