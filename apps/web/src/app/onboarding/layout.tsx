import { getTranslations } from 'next-intl/server';
import { redirect } from 'next/navigation';

import { TwoColumnFullPageLayout } from '@/components/layout/TwoColumnLayout';
import { isOnboarded } from '@/lib/auth';
import { requireSession } from '@/lib/auth/guards';
import ROUTES from '@/util/routes';

interface OnboardingLayoutProps {
  children?: React.ReactNode;
}

export default async function OnboardingLayout({
  children,
}: OnboardingLayoutProps) {
  const session = await requireSession();

  if (isOnboarded(session)) {
    redirect(ROUTES.HOME());
  }

  const t = await getTranslations('pages.onboarding.brand');

  return (
    <TwoColumnFullPageLayout
      brandTitle={t('title')}
      brandDescription={t('description')}
    >
      {children}
    </TwoColumnFullPageLayout>
  );
}
