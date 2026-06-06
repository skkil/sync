import { headers } from 'next/headers';
import { redirect } from 'next/navigation';

import { auth, isAuthenticated, isOnboarded } from '@/lib/auth';

import Notifications from './_components/Notifications';

export default async function NotificationsPage() {
  const session = await auth.api.getSession({
    headers: await headers(),
  });

  if (!isAuthenticated(session)) {
    redirect('/auth/login');
  }

  if (!isOnboarded(session)) {
    redirect('/onboarding');
  }

  return <Notifications />;
}
