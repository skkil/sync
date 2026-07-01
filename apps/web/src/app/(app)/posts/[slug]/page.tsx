import { HydrationBoundary, dehydrate } from '@tanstack/react-query';
import { redirect } from 'next/navigation';

import { getGetPostCommentsQueryOptions } from '@/api/__generated__/comment/comment';
import { getGetPostBySlugQueryOptions } from '@/api/__generated__/post/post';
import PostCardContainer from '@/components/feature/post/viewer/PostCardContainer';
import PostComments from '@/components/feature/post/viewer/PostComments';
import SyncError from '@/lib/error';
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

  try {
    const { data: post } = await queryClient.fetchQuery(
      getGetPostBySlugQueryOptions(slug),
    );

    if (post.project) {
      redirect(ROUTES.PROJECT_POST(post.project.handle, post.slug));
    }
  } catch (error) {
    if (error instanceof SyncError) {
    }
  }

  await queryClient.prefetchQuery(getGetPostBySlugQueryOptions(slug));
  await queryClient.prefetchQuery(getGetPostCommentsQueryOptions(slug));

  return (
    <HydrationBoundary state={dehydrate(queryClient)}>
      <PostCardContainer slug={slug} />
      <PostComments slug={slug} />
    </HydrationBoundary>
  );
}
