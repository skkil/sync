import { Metadata } from 'next';
import { getTranslations } from 'next-intl/server';

interface DraftPostsLayoutProps {
  children: React.ReactNode;
}

export async function generateMetadata(): Promise<Metadata> {
  const t = await getTranslations('pages.posts.drafts');

  return { title: t('title') };
}

export default function DraftPostsLayout({ children }: DraftPostsLayoutProps) {
  return children;
}
