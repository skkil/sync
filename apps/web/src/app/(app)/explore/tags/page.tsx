import { requireOnboardedSession } from '@/lib/auth/guards';

export default async function ExploreTagsPage() {
  await requireOnboardedSession();

  return null;
}
