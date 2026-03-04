import { PlusIcon, UsersIcon } from '@phosphor-icons/react/ssr';
import { headers } from 'next/headers';
import Link from 'next/link';

import { Button } from '@/components/ui/button';
import Logo from '@/components/ui/logo';
import { auth } from '@/lib/auth';

import { MessagesButton } from './_components/MessageButton';
import SearchBar from './_components/SearchBar';
import UserAvatar from './_components/UserAvatar';

export default async function NavigationBar() {
  const session = await auth.api.getSession({
    headers: await headers(),
  });

  return (
    <nav className="flex w-full items-center justify-between bg-background p-3 px-8">
      <Link href="/">
        <Logo />
      </Link>

      <div className="flex items-center gap-2">
        <SearchBar />

        {session && (
          <>
            <Link href="/provider/create">
              <Button variant="ghost" size="icon">
                <PlusIcon />
              </Button>
            </Link>

            <Link href="/network">
              <Button variant="ghost" size="icon">
                <UsersIcon />
              </Button>
            </Link>

            <MessagesButton />
          </>
        )}

        <UserAvatar />
      </div>
    </nav>
  );
}
