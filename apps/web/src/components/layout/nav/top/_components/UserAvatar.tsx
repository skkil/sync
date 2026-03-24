'use client';

import { GearSixIcon, SignOutIcon, UserIcon } from '@phosphor-icons/react';
import { useTranslations } from 'next-intl';
import Link from 'next/link';
import { useRouter } from 'next/navigation';

import { Avatar, AvatarFallback, AvatarImage } from '@/components/ui/avatar';
import { Button } from '@/components/ui/button';
import {
  DropdownMenu,
  DropdownMenuContent,
  DropdownMenuGroup,
  DropdownMenuItem,
  DropdownMenuTrigger,
} from '@/components/ui/dropdown-menu';
import { Spinner } from '@/components/ui/spinner';
import { ModalType } from '@/constants/modal';
import { useModal } from '@/hooks/store';
import { useSession } from '@/lib/auth/client';

interface UserAvatarProps {
  align?: 'start' | 'end';
}

export default function UserAvatar({ align = 'end' }: UserAvatarProps) {
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

  const menu = [
    {
      icon: UserIcon,
      label: t('user.profile'),
      onClick: () => {
        router.push(`/profile/${session.user.id}`);
      },
    },
    {
      icon: GearSixIcon,
      label: t('user.settings'),
      onClick: () => {
        openModal(ModalType.SETTINGS);
      },
    },
    {
      icon: SignOutIcon,
      label: t('user.sign-out'),
      onClick: () => {
        // TODO: need to implement sign out
      },
    },
  ];

  return (
    <DropdownMenu>
      <DropdownMenuTrigger asChild>
        <Button variant="ghost" size="icon" className="rounded-full">
          <Avatar>
            <AvatarImage src={session.user.image ?? undefined} />
            <AvatarFallback>{session.user.name[0]}</AvatarFallback>
          </Avatar>
        </Button>
      </DropdownMenuTrigger>

      <DropdownMenuContent align={align}>
        <DropdownMenuGroup>
          {menu.map((item, index) => {
            const Icon = item.icon;
            return (
              <DropdownMenuItem key={index} onClick={item.onClick}>
                <Icon />
                {item.label}
              </DropdownMenuItem>
            );
          })}
        </DropdownMenuGroup>
      </DropdownMenuContent>
    </DropdownMenu>
  );
}
