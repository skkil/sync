'use client';

import { useGetRecommendations } from '@/api/__generated__/user/user';
import {
  useFollowUser,
  useFollowedRecommendedUserIds,
} from '@/components/feature/user/hooks/useFollowUser';
import { Button } from '@/components/ui/button';
import {
  Carousel,
  CarouselContent,
  CarouselItem,
  CarouselNext,
  CarouselPrevious,
} from '@/components/ui/carousel';
import { Skeleton } from '@/components/ui/skeleton';

const MAX_USERS_PER_PAGE = 5;

export default function RecommendedUsers() {
  const followedUserIds = useFollowedRecommendedUserIds();

  const { data, isPending } = useGetRecommendations();
  const { mutate: followUser, isPending: isFollowPending } = useFollowUser();

  if (isPending) {
    return <RecommendedUsersSkeleton />;
  }

  const users = data?.data.users ?? [];

  if (users.length === 0) {
    return null;
  }

  return (
    <div className="flex flex-col gap-4">
      <p className="text-lg font-semibold">추천 사용자</p>

      <Carousel opts={{ align: 'start' }}>
        <CarouselContent>
          {users.map((user) => (
            <CarouselItem
              key={user.userId}
              className="basis-1/2 sm:basis-1/3 md:basis-1/4 lg:basis-1/5"
            >
              <div className="flex flex-col items-center gap-3 rounded-lg border p-4 text-center">
                <div className="bg-primary text-primary-foreground flex size-14 items-center justify-center rounded-2xl text-xl font-semibold">
                  {user.summary.name.charAt(0).toUpperCase()}
                </div>

                <div>
                  <p className="truncate text-sm font-semibold">
                    {user.summary.name}
                  </p>
                  <p className="text-muted-foreground truncate text-xs">
                    @{user.summary.handle}
                  </p>
                </div>

                <Button
                  className="w-full"
                  size="sm"
                  variant={
                    followedUserIds.includes(user.userId)
                      ? 'outline'
                      : 'default'
                  }
                  disabled={
                    isFollowPending || followedUserIds.includes(user.userId)
                  }
                  onClick={() => followUser({ followeeId: user.userId })}
                >
                  {followedUserIds.includes(user.userId) ? '팔로잉' : '팔로우'}
                </Button>
              </div>
            </CarouselItem>
          ))}
        </CarouselContent>

        {users.length > MAX_USERS_PER_PAGE && (
          <>
            <CarouselPrevious />
            <CarouselNext />
          </>
        )}
      </Carousel>
    </div>
  );
}

function RecommendedUsersSkeleton() {
  return (
    <div className="flex flex-col gap-4">
      <Skeleton className="h-6 w-24" />

      <div className="flex gap-4">
        {Array.from({ length: 5 }).map((_, index) => (
          <div
            key={index}
            className="flex basis-1/5 flex-col items-center gap-3 rounded-lg border p-4"
          >
            <Skeleton className="size-14 rounded-2xl" />
            <Skeleton className="h-4 w-16" />
            <Skeleton className="h-8 w-full" />
          </div>
        ))}
      </div>
    </div>
  );
}
