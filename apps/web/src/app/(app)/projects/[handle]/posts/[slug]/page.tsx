import { HydrationBoundary, dehydrate } from '@tanstack/react-query';
import { notFound } from 'next/navigation';

import { getGetPostCommentsInfiniteQueryOptions } from '@/api/__generated__/comment/comment';
import { getGetPostBySlugQueryOptions } from '@/api/__generated__/post/post';
import PostCardContainer from '@/components/feature/post/viewer/PostCardContainer';
import PostComments from '@/components/feature/post/viewer/PostComments';
import { COMMENT_PAGE_SIZE } from '@/components/feature/post/viewer/constants';
import { TwoColumnLayout } from '@/components/layout/TwoColumnLayout';
import SyncError, { ErrorCode } from '@/lib/error';
import { getQueryClient } from '@/lib/query';

interface PostProps {
  params: Promise<{
    slug: string;
  }>;
}

export default async function Post({ params }: PostProps) {
  const { slug } = await params;

  const queryClient = getQueryClient();
  let commentsEnabled = false;

  try {
    const { data: post } = await queryClient.fetchQuery(
      getGetPostBySlugQueryOptions(slug),
    );
    commentsEnabled = post.summary.status === 'PUBLISHED';
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
        main={<PostCardContainer slug={slug} />}
        side={commentsEnabled ? <PostComments slug={slug} /> : null}
      />
    </HydrationBoundary>
  );
}
