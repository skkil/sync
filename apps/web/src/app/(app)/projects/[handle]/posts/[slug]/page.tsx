import { HydrationBoundary, dehydrate } from '@tanstack/react-query';

import { getGetPostCommentsQueryOptions } from '@/api/__generated__/comment/comment';
import { getGetPostBySlugQueryOptions } from '@/api/__generated__/post/post';
import PostCardContainer from '@/components/feature/post/viewer/PostCardContainer';
import PostComments from '@/components/feature/post/viewer/PostComments';
import { getQueryClient } from '@/lib/query';

interface PostProps {
  params: Promise<{
    slug: string;
  }>;
}

export default async function Post({ params }: PostProps) {
  const { slug } = await params;

  const queryClient = getQueryClient();
  await queryClient.prefetchQuery(getGetPostBySlugQueryOptions(slug));
  await queryClient.prefetchQuery(getGetPostCommentsQueryOptions(slug));

  return (
    <HydrationBoundary state={dehydrate(queryClient)}>
      <PostCardContainer slug={slug} />
      <PostComments slug={slug} />
    </HydrationBoundary>
  );
}
