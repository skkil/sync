import { Metadata } from 'next';
import { getTranslations } from 'next-intl/server';

interface LoginLayoutProps {
  children: React.ReactNode;
}

export async function generateMetadata(): Promise<Metadata> {
  const t = await getTranslations('pages.login');

  return { title: t('title') };
}

export default function LoginLayout({ children }: LoginLayoutProps) {
  return children;
}
