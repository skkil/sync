import { headers } from 'next/headers';
import { redirect } from 'next/navigation';

import { auth, isAuthenticated, isOnboarded } from '@/lib/auth';

import Editor from './_components/Editor';

export default async function PostEditor() {
  const session = await auth.api.getSession({
    headers: await headers(),
  });

  if (isAuthenticated(session) && !isOnboarded(session)) {
    redirect('/onboarding');
  }

  if (!isAuthenticated(session)) {
    redirect('/auth/login');
  }

  return (
    <div className="h-full">
      <Editor />
    </div>
  );
}
