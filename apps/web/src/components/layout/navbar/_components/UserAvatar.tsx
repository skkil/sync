'use client';

import {
  GearSixIcon,
  SignOutIcon,
  UserGearIcon,
  UserIcon,
} from '@phosphor-icons/react';
import { useTranslations } from 'next-intl';
import { useRouter } from 'next/navigation';

import { useLogout } from '@/api/__generated__/auth/auth';
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
import { isAuthenticated } from '@/lib/auth';
import { signOut, useSession } from '@/lib/auth/client';

interface UserAvatarProps {
  align?: 'start' | 'end';
}

export default function UserAvatar({ align = 'end' }: UserAvatarProps) {
  const router = useRouter();

  const { mutateAsync: logout } = useLogout();

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

  if (!isAuthenticated(session)) {
    return null;
  }

  const menu = [
    {
      icon: UserIcon,
      isAdmin: false,
      label: t('user.profile'),
      onClick: () => {
        router.push(`/@${session.user.handle}`);
      },
    },
    {
      icon: GearSixIcon,
      isAdmin: false,
      label: t('user.settings'),
      onClick: () => {
        openModal(ModalType.SETTINGS);
      },
    },
    {
      icon: UserGearIcon,
      isAdmin: true,
      label: t('user.admin'),
      onClick: () => {
        router.push('/admin');
      },
    },
    {
      icon: SignOutIcon,
      isAdmin: false,
      label: t('user.sign-out'),
      onClick: async () => {
        try {
          await logout();
        } finally {
          await signOut();
          router.push('/');
        }
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
          {menu.map((item) => {
            const Icon = item.icon;

            if (item.isAdmin && session.user.role !== 'ADMIN') {
              return null;
            }

            return (
              <DropdownMenuItem key={item.label} onClick={item.onClick}>
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
