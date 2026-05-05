'use client';

import { useGetReflectionComments } from '@/api/__generated__/comment/comment';
import { Card } from '@/components/ui/card';
import { Skeleton } from '@/components/ui/skeleton';

interface PostCommentsProps {
  slug: string;
}

export default function PostComments({ slug }: PostCommentsProps) {
  const { data, isPending } = useGetReflectionComments(slug);

  if (isPending) {
    return <Skeleton className="h-96 w-full" />;
  }

  if (!data) {
    return null;
  }

  const comments = data.data.comments;

  return (
    <div>
      {comments.map((comment) => (
        <Card key={comment.id}>{comment.content}</Card>
      ))}
    </div>
  );
}
