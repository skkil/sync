import { requireOnboardedSession } from '@/lib/auth/guards';

export default async function ExploreProjectsPage() {
  await requireOnboardedSession();

  return null;
}
