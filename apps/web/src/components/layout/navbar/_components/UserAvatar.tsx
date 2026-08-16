'use client';

import {
  GearSixIcon,
  GiftIcon,
  SignOutIcon,
  UserGearIcon,
  UserIcon,
} from '@phosphor-icons/react';
import { useQueryClient } from '@tanstack/react-query';
import { useTranslations } from 'next-intl';
import { useRouter } from 'next/navigation';
import { toast } from 'sonner';

import { useLogout } from '@/api/__generated__/auth/auth';
import { ProfileAvatar } from '@/components/feature/profile/ProfileAvatar';
import { Avatar } from '@/components/ui/avatar';
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
import { useSession } from '@/lib/auth/client';
import { isAuthenticated } from '@/lib/auth/utils';
import { resetStompConnection } from '@/lib/ws';
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

  const handle = session.user.handle;

  const menu = [
    ...(handle
      ? [
          {
            icon: UserIcon,
            isAdmin: false,
            label: t('user.profile'),
            onClick: () => {
              router.push(ROUTES.PROFILE(handle));
            },
          },
        ]
      : []),
    {
      icon: GearSixIcon,
      isAdmin: false,
      label: t('user.settings'),
      onClick: () => {
        openModal(ModalType.SETTINGS);
      },
    },
    {
      icon: GiftIcon,
      isAdmin: false,
      label: t('user.promotions'),
      onClick: () => {
        openModal(ModalType.PROMOTIONS);
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

        // STOMP Principal은 핸드셰이크 시점에 고정되므로, 소켓을 즉시 버려야
        // 곧바로 다른 계정으로 로그인해도 이전 세션의 연결이 재사용되지 않는다.
        resetStompConnection();
        queryClient.clear();
        router.replace(ROUTES.HOME());
      },
    },
  ];

  return (
    <DropdownMenu>
      <DropdownMenuTrigger asChild>
        <Button variant="ghost" size="icon" className="rounded-full">
          <ProfileAvatar
            name={session.user.name}
            imageUrl={session.user.image}
          />
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
