'use client';

import {
  GearSixIcon,
  SignOutIcon,
  UserGearIcon,
  UserIcon,
} from '@phosphor-icons/react';
import { useQueryClient } from '@tanstack/react-query';
import { useTranslations } from 'next-intl';
import { useRouter } from 'next/navigation';
import { toast } from 'sonner';

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
import { useMounted } from '@/hooks/use-mounted';
import { isAuthenticated } from '@/lib/auth';
import { signOut, useSession } from '@/lib/auth/client';
import ROUTES from '@/util/routes';

interface UserAvatarProps {
  align?: 'start' | 'end';
}

export default function UserAvatar({ align = 'end' }: UserAvatarProps) {
  const router = useRouter();
  const queryClient = useQueryClient();

  const { mutateAsync: logout } = useLogout();

  const { isPending, data: session } = useSession();
  const { openModal } = useModal();

  const t = useTranslations('components.navigation');

  const mounted = useMounted();

  // `useSession` can resolve synchronously from a client-only cache, which
  // would make the very first client render diverge from the SSR output.
  // Rendering the pending state until mounted keeps hydration in sync.
  if (!mounted || isPending) {
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
        router.push(ROUTES.PROFILE(session.user.handle));
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
        router.push(ROUTES.ADMIN());
      },
    },
    {
      icon: SignOutIcon,
      isAdmin: false,
      label: t('user.sign-out'),
      onClick: async () => {
        try {
          await logout();
        } catch {
          toast.error(t('user.errors.sign-out'));
          return;
        }

        await signOut();
        queryClient.clear();
        router.replace(ROUTES.HOME());
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
