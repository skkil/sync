'use client';

import Link from 'next/link';
import * as React from 'react';

import { useGetProfileByHandle } from '@/api/__generated__/profile/profile';
import { Avatar, AvatarFallback, AvatarImage } from '@/components/ui/avatar';
import {
  Popover,
  PopoverContent,
  PopoverTrigger,
} from '@/components/ui/popover';
import { cn } from '@/lib/utils';

interface UserAvatarHoverCardProps {
  handle: string;
  name: string;
  imageUrl?: string;
  className?: string;
  size?: 'default' | 'sm' | 'lg';
}

export function UserAvatarHoverCard({
  handle,
  name,
  imageUrl,
  className,
  size = 'default',
}: UserAvatarHoverCardProps) {
  const [open, setOpen] = React.useState(false);
  const closeTimer = React.useRef<ReturnType<typeof setTimeout>>(null);

  const { data, isLoading } = useGetProfileByHandle(handle, {
    query: { enabled: open },
  });

  const profile = data?.data;

  function handleMouseEnter() {
    if (closeTimer.current) clearTimeout(closeTimer.current);
    setOpen(true);
  }

  function handleMouseLeave() {
    closeTimer.current = setTimeout(() => setOpen(false), 150);
  }

  return (
    <Popover open={open} onOpenChange={setOpen}>
      <PopoverTrigger asChild>
        <Avatar
          size={size}
          className={cn('cursor-pointer', className)}
          onMouseEnter={handleMouseEnter}
          onMouseLeave={handleMouseLeave}
        >
          <AvatarImage src={imageUrl} alt={name} />
          <AvatarFallback>{name[0]}</AvatarFallback>
        </Avatar>
      </PopoverTrigger>

      <PopoverContent
        side="bottom"
        align="start"
        onMouseEnter={handleMouseEnter}
        onMouseLeave={handleMouseLeave}
        onOpenAutoFocus={(e) => e.preventDefault()}
      >
        <div className="flex flex-col gap-3">
          <Link href={`/@${handle}`}>
            <Avatar size="lg">
              <AvatarImage
                src={
                  isLoading ? imageUrl : (profile?.profileImageUrl ?? imageUrl)
                }
                alt={name}
              />
              <AvatarFallback>{name[0]}</AvatarFallback>
            </Avatar>

            <div className="flex flex-col gap-0.5">
              <span className="font-medium">
                {isLoading ? name : (profile?.name ?? name)}
              </span>
              <span className="text-muted-foreground">@{handle}</span>
            </div>
          </Link>

          {!isLoading && profile?.bio && (
            <p className="text-muted-foreground line-clamp-3">{profile.bio}</p>
          )}
        </div>
      </PopoverContent>
    </Popover>
  );
}
