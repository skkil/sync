import { HydrationBoundary, dehydrate } from '@tanstack/react-query';
import { Metadata } from 'next';
import { getTranslations } from 'next-intl/server';
import { notFound } from 'next/navigation';

import { getGetPostCommentsInfiniteQueryOptions } from '@/api/__generated__/comment/comment';
import { getGetPostBySlugQueryKey } from '@/api/__generated__/post/post';
import { COMMENT_PAGE_SIZE } from '@/components/feature/post/constants';
import type { PostType } from '@/components/feature/post/types/post';
import { PostCard } from '@/components/feature/post/viewer/PostCard';
import PostComments from '@/components/feature/post/viewer/PostComments';
import { TwoColumnLayout } from '@/components/layout/TwoColumnLayout';
import SyncError, { ErrorCode } from '@/lib/error';
import { getPostBySlugCached } from '@/lib/post-query';
import { getQueryClient } from '@/lib/query';
import { NON_INDEXABLE_METADATA, createPostMetadata } from '@/lib/seo';

interface PostProps {
  params: Promise<{
    slug: string;
  }>;
}

export async function generateMetadata({
  params,
}: PostProps): Promise<Metadata> {
  const { slug } = await params;
  const t = await getTranslations('metadata');

  try {
    const { data: post } = await getPostBySlugCached(slug);

    return createPostMetadata(post.summary, t('description'));
  } catch {
    return NON_INDEXABLE_METADATA;
  }
}

export default async function Post({ params }: PostProps) {
  const { slug } = await params;

  const queryClient = getQueryClient();
  let commentsEnabled = false;
  let postType: PostType | undefined;
  let isPostAuthor = false;

  try {
    const response = await getPostBySlugCached(slug);
    const post = response.data;

    queryClient.setQueryData(getGetPostBySlugQueryKey(slug), response);
    commentsEnabled = post.summary.status === 'PUBLISHED';
    postType = post.summary.type as PostType;
    isPostAuthor = post.summary.isAuthor;
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
            <PostComments
              slug={slug}
              postType={postType}
              isPostAuthor={isPostAuthor}
            />
          ) : null
        }
      />
    </HydrationBoundary>
  );
}
