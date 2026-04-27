'use client';

import { useIntersectionObserver } from '@uidotdev/usehooks';
import Link from 'next/link';
import { useEffect } from 'react';

import { useGetTeamBuildingPostsInfinite } from '@/api/__generated__/team-building/team-building';
import type { GetTeamBuildingPostsResponsePostsNodesItem } from '@/api/__generated__/types';
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
      {
        first: TEAM_BUILDING_PAGE_SIZE,
        after: '',
      },
      {
        query: {
          getNextPageParam: (lastPage) => {
            const posts = lastPage.data.posts;
            return posts?.pageInfo.hasNextPage
              ? posts.pageInfo.endCursor
              : undefined;
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
    data?.pages.flatMap((page) => page.data.posts?.nodes ?? []) ?? [];

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
          <TeamBuildingPostCard key={post.content.id} post={post} />
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
  post: GetTeamBuildingPostsResponsePostsNodesItem;
}) {
  const { title, project } = post.content;

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
                href={`/projects/${project.id}`}
                className="text-sm font-medium text-muted-foreground"
              >
                {project.name}
              </Link>
            </TooltipTrigger>

            <TooltipContent align="start">{project.description}</TooltipContent>
          </Tooltip>
        </div>

        <h3>{title}</h3>
      </div>
    </div>
  );
}

function TeamBuildingPostSkeleton() {
  return <Skeleton className="h-20 w-full" />;
}
