import { requireOnboardedSession } from '@/lib/auth/guards';

import Posts from './_components/Posts';

export default async function Home() {
  await requireOnboardedSession();

  return (
    <div>
      <Posts />
    </div>
  );
}
