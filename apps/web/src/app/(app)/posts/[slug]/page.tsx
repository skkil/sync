import { HydrationBoundary, dehydrate } from '@tanstack/react-query';

import { getGetReflectionCommentsQueryOptions } from '@/api/__generated__/comment/comment';
import { getGetReflectionBySlugQueryOptions } from '@/api/__generated__/reflection/reflection';
import { getQueryClient } from '@/lib/query';

import PostComments from './_components/PostComments';
import PostContent from './_components/PostContent';

interface PostProps {
  params: Promise<{
    slug: string;
  }>;
}

export default async function Post({ params }: PostProps) {
  const { slug } = await params;

  const queryClient = getQueryClient();
  await queryClient.prefetchQuery(getGetReflectionBySlugQueryOptions(slug));
  await queryClient.prefetchQuery(getGetReflectionCommentsQueryOptions(slug));

  return (
    <HydrationBoundary state={dehydrate(queryClient)}>
      <PostContent slug={slug} />
      <PostComments slug={slug} />
    </HydrationBoundary>
  );
}
