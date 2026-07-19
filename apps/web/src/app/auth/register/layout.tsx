import { Metadata } from 'next';
import { getTranslations } from 'next-intl/server';

interface RegisterLayoutProps {
  children: React.ReactNode;
}

export async function generateMetadata(): Promise<Metadata> {
  const t = await getTranslations('pages.register');

  return { title: t('title') };
}

export default function RegisterLayout({ children }: RegisterLayoutProps) {
  return children;
}
