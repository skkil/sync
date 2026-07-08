'use client';

import { useGetPostBySlug } from '@/api/__generated__/post/post';
import PostCard from '@/components/feature/post/viewer/PostCard';
import { Skeleton } from '@/components/ui/skeleton';

import { PostStatus, PostType } from '../types/post';

interface PostCardContainerProps {
  slug: string;
}

export default function PostCardContainer({ slug }: PostCardContainerProps) {
  const { data, isPending } = useGetPostBySlug(slug);

  if (isPending || !data) {
    return <Skeleton className="h-40 w-full" />;
  }

  const post = data.data;
  const { summary } = post;

  return (
    <PostCard
      id={summary.id}
      slug={slug}
      type={summary.type as PostType}
      status={summary.status as PostStatus}
      title={summary.title}
      author={summary.author}
      project={summary.project}
      content={post.content}
      liked={summary.liked}
      likeCount={summary.likeCount}
      commentCount={summary.commentCount}
      bookmarked={summary.bookmarked}
      isAuthor={summary.isAuthor}
      createdAt={summary.createdAt}
    />
  );
}
