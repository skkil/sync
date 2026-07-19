import { Metadata } from 'next';
import { getTranslations } from 'next-intl/server';

import { requireOnboardedSession } from '@/lib/auth/guards';

import BookmarkedPosts from './_components/BookmarkedPosts';

export async function generateMetadata(): Promise<Metadata> {
  const t = await getTranslations('pages.bookmarks');

  return { title: t('title') };
}

export default async function BookmarksPage() {
  await requireOnboardedSession();

  return <BookmarkedPosts />;
}
