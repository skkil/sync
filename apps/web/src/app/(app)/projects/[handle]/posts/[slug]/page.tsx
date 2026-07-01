import { HydrationBoundary, dehydrate } from '@tanstack/react-query';
import { notFound } from 'next/navigation';

import { getGetPostCommentsQueryOptions } from '@/api/__generated__/comment/comment';
import { getGetPostBySlugQueryOptions } from '@/api/__generated__/post/post';
import PostCardContainer from '@/components/feature/post/viewer/PostCardContainer';
import PostComments from '@/components/feature/post/viewer/PostComments';
import TwoColumnLayout from '@/components/layout/TwoColumnLayout';
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

  try {
    await queryClient.fetchQuery(getGetPostBySlugQueryOptions(slug));
  } catch (error) {
    if (error instanceof SyncError) {
      switch (error.code) {
        case ErrorCode.POST_NOT_FOUND:
          notFound();
      }
    }

    throw error;
  }

  await queryClient.prefetchQuery(getGetPostCommentsQueryOptions(slug));

  return (
    <HydrationBoundary state={dehydrate(queryClient)}>
      <TwoColumnLayout
        main={<PostCardContainer slug={slug} />}
        side={<PostComments slug={slug} />}
      />
    </HydrationBoundary>
  );
}
