import { UserGearIcon } from '@phosphor-icons/react/dist/ssr';
import { getTranslations } from 'next-intl/server';
import { headers } from 'next/headers';
import Link from 'next/link';

import { auth } from '@/lib/auth';

export default async function HomeRightSidebar() {
  const session = await auth.api.getSession({
    headers: await headers(),
  });

  const t = await getTranslations('pages.home.right');

  return (
    <div className="flex flex-col gap-6 mt-6">
      {session && (
        <Link href="/provider/my">
          <div className="flex w-full items-center gap-2 mx-2">
            <UserGearIcon />
            {t('my-providers')}
          </div>
        </Link>
      )}
    </div>
  );
}
