import { Metadata } from 'next';
import { getTranslations } from 'next-intl/server';

interface PostReportsLayoutProps {
  children: React.ReactNode;
}

export async function generateMetadata(): Promise<Metadata> {
  const t = await getTranslations('pages.admin.post-reports');

  return { title: t('title') };
}

export default function PostReportsLayout({
  children,
}: PostReportsLayoutProps) {
  return children;
}
