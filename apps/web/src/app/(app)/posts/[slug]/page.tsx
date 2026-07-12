import { HydrationBoundary, dehydrate } from '@tanstack/react-query';
import { notFound, redirect } from 'next/navigation';

import { getGetPostCommentsInfiniteQueryOptions } from '@/api/__generated__/comment/comment';
import { getGetPostBySlugQueryOptions } from '@/api/__generated__/post/post';
import type { PostType } from '@/components/feature/post/types/post';
import { PostCard } from '@/components/feature/post/viewer/PostCard';
import PostComments, {
  COMMENT_PAGE_SIZE,
} from '@/components/feature/post/viewer/PostComments';
import { TwoColumnLayout } from '@/components/layout/TwoColumnLayout';
import SyncError, { ErrorCode } from '@/lib/error';
import { getQueryClient } from '@/lib/query';
import ROUTES from '@/util/routes';

interface PostProps {
  params: Promise<{
    slug: string;
  }>;
}

export default async function Post({ params }: PostProps) {
  const { slug } = await params;

  const queryClient = getQueryClient();
  let commentsEnabled = false;
  let postType: PostType | undefined;

  try {
    const { data: post } = await queryClient.fetchQuery(
      getGetPostBySlugQueryOptions(slug),
    );

    commentsEnabled = post.summary.status === 'PUBLISHED';
    postType = post.summary.type as PostType;

    if (post.summary.project?.handle) {
      redirect(ROUTES.PROJECT_POST(post.summary.project.handle, slug));
    }
  } catch (error) {
    if (error instanceof SyncError) {
      switch (error.code) {
        case ErrorCode.POST_NOT_FOUND:
          notFound();
      }
    }

    throw error;
  }

  if (commentsEnabled) {
    await queryClient.prefetchInfiniteQuery(
      getGetPostCommentsInfiniteQueryOptions(
        slug,
        { first: COMMENT_PAGE_SIZE },
        {
          query: {
            getNextPageParam: (lastPage) => {
              const pageInfo = lastPage.data.comments?.pageInfo;
              return pageInfo?.hasNextPage
                ? (pageInfo.endCursor ?? undefined)
                : undefined;
            },
          },
        },
      ),
    );
  }

  return (
    <HydrationBoundary state={dehydrate(queryClient)}>
      <TwoColumnLayout
        main={<PostCard slug={slug} />}
        side={
          commentsEnabled && postType ? (
            <PostComments slug={slug} postType={postType} />
          ) : null
        }
      />
    </HydrationBoundary>
  );
}
