import { requireOnboardedSession } from '@/lib/auth/guards';

import BookmarkedPosts from './_components/BookmarkedPosts';

export default async function BookmarksPage() {
  await requireOnboardedSession();

  return <BookmarkedPosts />;
}
