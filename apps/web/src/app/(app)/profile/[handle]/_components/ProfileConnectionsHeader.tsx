'use client';

import { CaretLeftIcon } from '@phosphor-icons/react';
import Link from 'next/link';

import { useGetProfileByHandle } from '@/api/__generated__/profile/profile';
import { Avatar, AvatarFallback, AvatarImage } from '@/components/ui/avatar';
import { Button } from '@/components/ui/button';
import { Skeleton } from '@/components/ui/skeleton';
import ROUTES from '@/util/routes';

interface ProfileConnectionsHeaderProps {
  handle: string;
}

export default function ProfileConnectionsHeader({
  handle,
}: ProfileConnectionsHeaderProps) {
  const { data: profile, isPending } = useGetProfileByHandle(handle);

  return (
    <div className="flex items-center gap-3">
      <Button type="button" variant="ghost" size="icon" asChild>
        <Link href={ROUTES.PROFILE(handle)}>
          <CaretLeftIcon />
        </Link>
      </Button>

      {isPending || !profile ? (
        <Skeleton className="h-6 w-32" />
      ) : (
        <div className="flex items-center gap-2">
          <Avatar size="sm">
            <AvatarImage
              src={profile.data.profileImageUrl ?? undefined}
              alt={profile.data.name}
            />
            <AvatarFallback>{profile.data.name[0]}</AvatarFallback>
          </Avatar>

          <div className="flex flex-col leading-tight">
            <span className="text-sm font-semibold">{profile.data.name}</span>
            <span className="text-xs text-muted-foreground">@{handle}</span>
          </div>
        </div>
      )}
    </div>
  );
}
