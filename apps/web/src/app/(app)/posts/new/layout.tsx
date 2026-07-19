import { Metadata } from 'next';
import { getTranslations } from 'next-intl/server';

interface CreatePostLayoutProps {
  children: React.ReactNode;
}

export async function generateMetadata(): Promise<Metadata> {
  const t = await getTranslations('pages.posts.new');

  return { title: t('title') };
}

export default function CreatePostLayout({ children }: CreatePostLayoutProps) {
  return children;
}
