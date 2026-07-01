import { headers } from 'next/headers';
import { redirect } from 'next/navigation';

import { auth, isAuthenticated, isOnboarded } from '@/lib/auth';

import Posts from './_components/Posts';

export default async function Feed() {
  const session = await auth.api.getSession({
    headers: await headers(),
  });

  if (!isAuthenticated(session)) {
    redirect('/auth/login');
  }

  if (!isOnboarded(session)) {
    redirect('/onboarding');
  }

  return (
    <div>
      <Posts />
    </div>
  );
}
