'use client';

import { useGetPostBySlug } from '@/api/__generated__/post/post';
import PostCard from '@/components/feature/post/viewer/PostCard';
import { Skeleton } from '@/components/ui/skeleton';
import { PostType } from '@/features/post/constants/post-type';

interface PostCardContainerProps {
  slug: string;
}

export default function PostCardContainer({ slug }: PostCardContainerProps) {
  const { data, isPending } = useGetPostBySlug(slug);

  if (isPending || !data) {
    return <Skeleton className="h-40 w-full" />;
  }

  const post = data.data;

  return (
    <PostCard
      id={post.id}
      type={post.type as PostType}
      author={post.author}
      project={post.project}
      content={post.content}
      likeCount={post.likeCount}
      commentCount={post.commentCount}
      bookmarked={post.bookmarked}
      createdAt={post.createdAt}
    />
  );
}
