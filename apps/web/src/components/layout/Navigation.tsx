import { ChatIcon } from '@phosphor-icons/react/dist/ssr';
import { getTranslations } from 'next-intl/server';
import { headers } from 'next/headers';
import Link from 'next/link';

import { Avatar, AvatarFallback } from '@/components/ui/avatar';
import { Button } from '@/components/ui/button';
import Logo from '@/components/ui/logo';
import { auth } from '@/lib/auth';

export default async function Navigation() {
  const t = await getTranslations('components.navigation');

  const session = await auth.api.getSession({
    headers: await headers(),
  });

  return (
    <nav className="flex w-full items-center justify-between border-b bg-white p-3 px-8">
      <Link href="/">
        <Logo />
      </Link>

      <div className="flex items-center gap-2">
        {session && (
          <Link href="/messages">
            <Button variant="ghost" size="icon">
              <ChatIcon />
            </Button>
          </Link>
        )}

        {session && session.user ? (
          <Link href={`/profile/${session.user.id}`}>
            <Avatar>
              <AvatarFallback>{session.user.name[0]}</AvatarFallback>
            </Avatar>
          </Link>
        ) : (
          <Link href="/auth/login">
            <Button variant="ghost">{t('login')}</Button>
          </Link>
        )}
      </div>
    </nav>
  );
}
