'use client';

import { useIntersectionObserver } from '@uidotdev/usehooks';
import Link from 'next/link';
import { useEffect } from 'react';

import { useGetTeamBuildingPostsInfinite } from '@/api/__generated__/team-building/team-building';
import { GetTeamBuildingPostsResponsePostsContentItem } from '@/api/__generated__/types';
import { Avatar, AvatarFallback } from '@/components/ui/avatar';
import { Skeleton } from '@/components/ui/skeleton';
import { Spinner } from '@/components/ui/spinner';
import {
  Tooltip,
  TooltipContent,
  TooltipTrigger,
} from '@/components/ui/tooltip';

const TEAM_BUILDING_PAGE_SIZE = '50';

export default function TeamBuildingPage() {
  const { data, fetchNextPage, hasNextPage, isFetchingNextPage, isPending } =
    useGetTeamBuildingPostsInfinite(
      { cursor: '', size: TEAM_BUILDING_PAGE_SIZE },
      {
        query: {
          getNextPageParam: (lastPage) => {
            const posts = lastPage.data.posts;
            return posts?.hasNext ? posts.nextCursor : undefined;
          },
        },
      },
    );

  const [ref, entry] = useIntersectionObserver({
    threshold: 0.2,
    root: null,
    rootMargin: '400px',
  });

  useEffect(() => {
    if (entry?.isIntersecting && hasNextPage && !isFetchingNextPage) {
      fetchNextPage();
    }
  }, [entry?.isIntersecting, hasNextPage, isFetchingNextPage, fetchNextPage]);

  const posts =
    data?.pages.flatMap((page) => page.data.posts?.content ?? []) ?? [];

  if (isPending) {
    return (
      <div className="space-y-4">
        {Array.from({ length: 3 }).map((_, i) => (
          <TeamBuildingPostSkeleton key={i} />
        ))}
      </div>
    );
  }

  return (
    <div>
      <div className="space-y-4">
        {posts.map((post) => (
          <TeamBuildingPostCard key={post.id} post={post} />
        ))}
      </div>

      <div ref={ref} className="py-4">
        {isFetchingNextPage && (
          <div className="flex justify-center">
            <Spinner />
          </div>
        )}
      </div>
    </div>
  );
}

function TeamBuildingPostCard({
  post,
}: {
  post: GetTeamBuildingPostsResponsePostsContentItem;
}) {
  return (
    <div className="flex items-center gap-4 p-2 rounded-md hover:bg-muted">
      <Avatar>
        <AvatarFallback></AvatarFallback>
      </Avatar>

      <div>
        <div>
          <Tooltip>
            <TooltipTrigger>
              <Link
                href={`/projects/${post.project.id}`}
                className="text-sm font-medium text-muted-foreground"
              >
                {post.project.name}
              </Link>
            </TooltipTrigger>

            <TooltipContent align="start">
              {post.project.description}
            </TooltipContent>
          </Tooltip>
        </div>

        <h3>{post.title}</h3>
      </div>
    </div>
  );
}

function TeamBuildingPostSkeleton() {
  return <Skeleton className="h-20 w-full" />;
}
