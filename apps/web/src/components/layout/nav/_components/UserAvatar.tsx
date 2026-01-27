'use client';

import { GearSixIcon, SignOutIcon, UserIcon } from '@phosphor-icons/react';
import { useTranslations } from 'next-intl';
import Link from 'next/link';
import { useRouter } from 'next/navigation';

import { Avatar, AvatarFallback } from '@/components/ui/avatar';
import { Button } from '@/components/ui/button';
import {
  DropdownMenu,
  DropdownMenuContent,
  DropdownMenuGroup,
  DropdownMenuItem,
  DropdownMenuSeparator,
  DropdownMenuTrigger,
} from '@/components/ui/dropdown-menu';
import { Spinner } from '@/components/ui/spinner';
import { ModalType } from '@/constants/modal';
import { useModal } from '@/hooks/store';
import { useSession } from '@/lib/auth/client';

export default function UserAvatar() {
  const router = useRouter();
  const { isPending, data: session } = useSession();
  const { openModal } = useModal();

  const t = useTranslations('components.navigation');

  if (isPending) {
    return (
      <Avatar className="flex items-center justify-center">
        <Spinner />
      </Avatar>
    );
  }

  if (!session || !session.user) {
    return (
      <Link href="/auth/login">
        <Button variant="ghost">{t('login')}</Button>
      </Link>
    );
  }

  return (
    <DropdownMenu>
      <DropdownMenuTrigger asChild>
        <Button variant="ghost" size="icon" className="rounded-full">
          <Avatar>
            <AvatarFallback>{session.user.name[0]}</AvatarFallback>
          </Avatar>
        </Button>
      </DropdownMenuTrigger>

      <DropdownMenuContent align="end">
        <DropdownMenuGroup>
          <DropdownMenuItem
            onClick={() => {
              router.push(`/profile/${session.user.id}`);
            }}
          >
            <UserIcon />
            {t('user.profile')}
          </DropdownMenuItem>

          <DropdownMenuItem
            onClick={() => {
              openModal(ModalType.SETTINGS);
            }}
          >
            <GearSixIcon />
            {t('user.settings')}
          </DropdownMenuItem>
        </DropdownMenuGroup>

        <DropdownMenuSeparator />

        <DropdownMenuItem
          onClick={() => {
            // TODO: need to implement sign out
          }}
        >
          <SignOutIcon />
          {t('user.sign-out')}
        </DropdownMenuItem>
      </DropdownMenuContent>
    </DropdownMenu>
  );
}
