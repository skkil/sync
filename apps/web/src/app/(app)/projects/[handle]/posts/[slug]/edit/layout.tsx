import { Metadata } from 'next';
import { getTranslations } from 'next-intl/server';

interface EditProjectPostLayoutProps {
  children: React.ReactNode;
}

export async function generateMetadata(): Promise<Metadata> {
  const t = await getTranslations('pages.posts.edit');

  return { title: t('title') };
}

export default function EditProjectPostLayout({
  children,
}: EditProjectPostLayoutProps) {
  return children;
}
