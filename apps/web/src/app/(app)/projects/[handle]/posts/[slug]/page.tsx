import { HydrationBoundary, dehydrate } from '@tanstack/react-query';
import { Metadata } from 'next';
import { getTranslations } from 'next-intl/server';
import { notFound } from 'next/navigation';

import { getGetPostCommentsInfiniteQueryOptions } from '@/api/__generated__/comment/comment';
import { getGetPostBySlugQueryKey } from '@/api/__generated__/post/post';
import { COMMENT_PAGE_SIZE } from '@/components/feature/post/constants';
import type { PostType } from '@/components/feature/post/types/post';
import { PostCard } from '@/components/feature/post/viewer/PostCard';
import { PostProvider } from '@/components/feature/post/viewer/PostContext';
import { PostViewerSidebar } from '@/components/feature/post/viewer/PostViewerSidebar';
import { RelatedPosts } from '@/components/feature/post/viewer/RelatedPosts';
import { TwoColumnLayout } from '@/components/layout/TwoColumnLayout';
import SyncError, { ErrorCode } from '@/lib/error';
import { getPostBySlugCached } from '@/lib/post-query';
import { getQueryClient } from '@/lib/query';
import {
  NON_INDEXABLE_METADATA,
  buildPostJsonLd,
  createPostMetadata,
  isPostIndexable,
} from '@/lib/seo';
import ROUTES from '@/util/routes';

interface PostProps {
  params: Promise<{
    handle: string;
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
  const { handle, slug } = await params;

  const queryClient = getQueryClient();
  let commentsEnabled = false;
  let postId: number | undefined;
  let postType: PostType | undefined;
  let isPostAuthor = false;
  let canComment = false;
  let requiresMembership = false;
  let jsonLd: Record<string, unknown> | null = null;

  try {
    const response = await getPostBySlugCached(slug);
    const post = response.data;

    queryClient.setQueryData(getGetPostBySlugQueryKey(slug), response);
    commentsEnabled = post.summary.status === 'PUBLISHED';
    postId = post.summary.id;
    postType = post.summary.type as PostType;
    isPostAuthor = post.summary.isAuthor;
    canComment = post.summary.canComment;
    requiresMembership = post.summary.scope === 'WORKSPACE';

    if (isPostIndexable(post.summary)) {
      jsonLd = buildPostJsonLd(post.summary, ROUTES.PROJECT_POST(handle, slug));
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
      {jsonLd && (
        <script
          type="application/ld+json"
          dangerouslySetInnerHTML={{ __html: JSON.stringify(jsonLd) }}
        />
      )}
      <PostProvider>
        <TwoColumnLayout
          main={<PostCard slug={slug} />}
          side={
            <PostViewerSidebar
              slug={slug}
              commentsEnabled={commentsEnabled}
              postId={postId}
              postType={postType}
              isPostAuthor={isPostAuthor}
              canComment={canComment}
              requiresMembership={requiresMembership}
            />
          }
        />
      </PostProvider>
      <RelatedPosts slug={slug} />
    </HydrationBoundary>
  );
}
