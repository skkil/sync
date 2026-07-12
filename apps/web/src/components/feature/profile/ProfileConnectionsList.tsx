'use client';

import { useTranslations } from 'next-intl';

import { useGetProfileByHandle } from '@/api/__generated__/profile/profile';
import {
  useGetFollowersInfinite,
  useGetFollowingInfinite,
} from '@/api/__generated__/user/user';
import { InfiniteProfileList } from '@/components/feature/profile/InfiniteProfileList';

const CONNECTIONS_PAGE_SIZE = '20';

interface ProfileConnectionsListProps {
  handle: string;
  type: 'followers' | 'following';
}

export function ProfileConnectionsList({
  handle,
  type,
}: ProfileConnectionsListProps) {
  const t = useTranslations('pages.profile.connections');

  const {
    data: profile,
    isPending: isProfilePending,
    isError: isProfileError,
  } = useGetProfileByHandle(handle);

  const userId = profile?.data.userId;

  const followers = useGetFollowersInfinite(
    userId ?? '',
    { first: CONNECTIONS_PAGE_SIZE, after: '' },
    {
      query: {
        enabled: !!userId && type === 'followers',
        getNextPageParam: (lastPage) => {
          const pageInfo = lastPage.data.connections?.pageInfo;
          return pageInfo?.hasNextPage
            ? (pageInfo.endCursor ?? undefined)
            : undefined;
        },
      },
    },
  );

  const following = useGetFollowingInfinite(
    userId ?? '',
    { first: CONNECTIONS_PAGE_SIZE, after: '' },
    {
      query: {
        enabled: !!userId && type === 'following',
        getNextPageParam: (lastPage) => {
          const pageInfo = lastPage.data.connections?.pageInfo;
          return pageInfo?.hasNextPage
            ? (pageInfo.endCursor ?? undefined)
            : undefined;
        },
      },
    },
  );

  const {
    data,
    fetchNextPage,
    hasNextPage,
    isFetchingNextPage,
    isPending: isConnectionsPending,
    isError: isConnectionsError,
  } = type === 'followers' ? followers : following;

  const nodes =
    data?.pages.flatMap((page) => page.data.connections?.nodes ?? []) ?? [];

  const users = nodes.map((node) => ({
    handle: node.content.handle,
    name: node.content.name,
    imageUrl: node.content.profileImageUrl,
  }));

  const isPending = isProfilePending || (!!userId && isConnectionsPending);
  const isError = isProfileError || isConnectionsError;

  return (
    <InfiniteProfileList
      users={users}
      isPending={isPending}
      isError={isError}
      hasNextPage={!!hasNextPage}
      isFetchingNextPage={isFetchingNextPage}
      fetchNextPage={fetchNextPage}
      empty={
        <div className="rounded-md border px-4 py-8 text-center">
          <p className="text-sm text-muted-foreground">{t(`${type}.empty`)}</p>
        </div>
      }
      error={
        <div className="rounded-md border px-4 py-8 text-center">
          <p className="text-sm text-destructive">{t(`${type}.error`)}</p>
        </div>
      }
    />
  );
}
