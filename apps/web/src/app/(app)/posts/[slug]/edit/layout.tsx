import { Metadata } from 'next';
import { getTranslations } from 'next-intl/server';

interface EditPostLayoutProps {
  children: React.ReactNode;
}

export async function generateMetadata(): Promise<Metadata> {
  const t = await getTranslations('pages.posts.edit');

  return { title: t('title') };
}

export default function EditPostLayout({ children }: EditPostLayoutProps) {
  return children;
}
