'use client';

import { useTranslations } from 'next-intl';

import { useGetTeamBuildingPostsByProjectInfinite } from '@/api/__generated__/team-building/team-building';
import { Button } from '@/components/ui/button';
import CommentsSection from '@/features/comment/components/CommentsSection';

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
        first: TEAM_BUILDING_POSTS_PAGE_SIZE,
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

  const posts =
    data?.pages.flatMap((page) => page.data.posts?.nodes ?? []) ?? [];

  if (data && posts.length === 0) {
    return <div>{t('empty')}</div>;
  }

  return (
    <div className="space-y-4">
      {posts.map((post) => (
        <div key={post.content.id} className="space-y-3 rounded-lg border p-4">
          <div>
            <h3 className="font-medium">{post.content.title}</h3>
            {post.content.content && (
              <p className="mt-2 whitespace-pre-wrap text-sm text-muted-foreground">
                {post.content.content}
              </p>
            )}
          </div>
          <CommentsSection
            targetType="TEAM_BUILDING_POST"
            targetId={post.content.id}
          />
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
