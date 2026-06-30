import { headers } from 'next/headers';
import { redirect } from 'next/navigation';

import MiniPostEditor from '@/components/feature/post/editor/MiniPostEditor';
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
      <div>
        <MiniPostEditor />
      </div>
      <div>
        <Posts />
      </div>
    </div>
  );
}
