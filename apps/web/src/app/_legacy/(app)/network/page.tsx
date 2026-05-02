import { getTranslations } from 'next-intl/server';
import { headers } from 'next/headers';
import { redirect } from 'next/navigation';

import { auth } from '@/lib/auth';

import ConnectionsTable from './_components/ConnectionsTable';

export default async function Network() {
  const t = await getTranslations('pages.network');

  const session = await auth.api.getSession({
    headers: await headers(),
  });

  if (!session) {
    redirect('/auth/login');
  }

  if (session && !session.user.isOnboarded) {
    redirect('/onboarding');
  }

  return (
    <div className="max-w-7xl mx-auto">
      <div className="flex flex-col gap-4">
        <h2 className="text-xl">{t('connections.title')}</h2>
        <ConnectionsTable />
      </div>
    </div>
  );
}
