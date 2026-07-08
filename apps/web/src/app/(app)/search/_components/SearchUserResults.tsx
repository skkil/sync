'use client';

import { MagnifyingGlassIcon } from '@phosphor-icons/react';
import { useTranslations } from 'next-intl';
import Link from 'next/link';

import { useSearchUsers } from '@/api/__generated__/user/user';
import { Avatar, AvatarFallback, AvatarImage } from '@/components/ui/avatar';
import { Card } from '@/components/ui/card';
import {
  Empty,
  EmptyHeader,
  EmptyMedia,
  EmptyTitle,
} from '@/components/ui/empty';
import { Skeleton } from '@/components/ui/skeleton';
import ROUTES from '@/util/routes';

interface SearchUserResultsProps {
  query: string;
  limit?: number;
}

export default function SearchUserResults({
  query,
  limit,
}: SearchUserResultsProps) {
  const t = useTranslations('pages.search');
  const { data, isPending } = useSearchUsers(
    { query },
    { query: { enabled: !!query } },
  );

  if (isPending) {
    return (
      <div className="grid grid-cols-2 gap-4 sm:grid-cols-3 md:grid-cols-4 lg:grid-cols-5">
        {Array.from({ length: 5 }).map((_, index) => (
          <Skeleton key={index} className="h-32 w-full" />
        ))}
      </div>
    );
  }

  const users = data?.data.users ?? [];
  const visibleUsers = limit ? users.slice(0, limit) : users;

  if (visibleUsers.length === 0) {
    return (
      <Empty className="min-h-60">
        <EmptyMedia variant="icon">
          <MagnifyingGlassIcon />
        </EmptyMedia>
        <EmptyHeader>
          <EmptyTitle>{t('results.no-results', { query })}</EmptyTitle>
        </EmptyHeader>
      </Empty>
    );
  }

  return (
    <div className="grid grid-cols-2 gap-4 sm:grid-cols-3 md:grid-cols-4 lg:grid-cols-5">
      {visibleUsers.map((user) => (
        <Link key={user.handle} href={ROUTES.PROFILE(user.handle)}>
          <Card className="items-center gap-3 p-4 text-center transition-colors hover:bg-muted/50">
            <Avatar className="size-14">
              <AvatarImage src={user.profileImageUrl ?? undefined} />
              <AvatarFallback>
                {user.name.charAt(0).toUpperCase()}
              </AvatarFallback>
            </Avatar>

            <div>
              <p className="truncate text-sm font-semibold">{user.name}</p>
              <p className="text-muted-foreground truncate text-xs">
                @{user.handle}
              </p>
            </div>
          </Card>
        </Link>
      ))}
    </div>
  );
}
