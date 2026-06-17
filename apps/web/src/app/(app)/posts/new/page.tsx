import { headers } from 'next/headers';
import { redirect } from 'next/navigation';

import PostEditor from '@/components/feature/post/editor/PostEditor';
import { auth, isAuthenticated, isOnboarded } from '@/lib/auth';

export default async function CreatePostPage() {
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
      <PostEditor />
    </div>
  );
}
