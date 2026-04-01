'use client';

import { useTranslations } from 'next-intl';

import { useGetTeamBuildingPostsByProjectInfinite } from '@/api/__generated__/team-building/team-building';
import { Button } from '@/components/ui/button';

const TEAM_BUILDING_POSTS_PAGE_SIZE = '20';

interface TeamBuildingPostsListProps {
  projectId: string;
}

export default function TeamBuildingPostList({
  projectId,
}: TeamBuildingPostsListProps) {
  const t = useTranslations('pages.project.team-building.list');

  const { data, fetchNextPage, hasNextPage, isFetchingNextPage } =
    useGetTeamBuildingPostsByProjectInfinite(
      projectId,
      {
        size: TEAM_BUILDING_POSTS_PAGE_SIZE,
      },
      {
        query: {
          getNextPageParam: (lastPage) => {
            const posts = lastPage.data.posts;
            return posts?.hasNext ? posts.nextCursor : undefined;
          },
        },
      },
    );

  const posts =
    data?.pages.flatMap((page) => page.data.posts?.content ?? []) ?? [];

  return (
    <div className="space-y-4">
      {posts.map((post) => (
        <div key={post.id} className="rounded-lg border p-4">
          {post.title}
        </div>
      ))}

      {hasNextPage && (
        <div className="flex justify-center">
          <Button onClick={() => fetchNextPage()} disabled={isFetchingNextPage}>
            {isFetchingNextPage ? t('loading') : t('load-more')}
          </Button>
        </div>
      )}
    </div>
  );
}
