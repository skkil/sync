import type { Metadata } from 'next';
import { getTranslations } from 'next-intl/server';
import { headers } from 'next/headers';
import { redirect } from 'next/navigation';

import { auth, isAuthenticated, isOnboarded } from '@/lib/auth';

import LandingPage from './_components/LandingPage';

export async function generateMetadata(): Promise<Metadata> {
  const t = await getTranslations('pages.landing.metadata');

  return {
    title: t('title'),
    description: t('description'),
  };
}

export default async function MarketingHome() {
  const session = await auth.api.getSession({
    headers: await headers(),
  });

  if (isAuthenticated(session) && !isOnboarded(session)) {
    redirect('/onboarding');
  }

  if (isAuthenticated(session)) {
    redirect('/feed');
  }

  return <LandingPage />;
}
