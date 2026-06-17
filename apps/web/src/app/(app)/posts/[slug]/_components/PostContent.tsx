'use client';

import {
  ChatCircleIcon,
  DotsThreeIcon,
  HeartIcon,
} from '@phosphor-icons/react';

import { useGetPostBySlug } from '@/api/__generated__/post/post';
import { BaseViewer } from '@/components/feature/post';
import { Avatar, AvatarFallback } from '@/components/ui/avatar';
import { Button } from '@/components/ui/button';
import {
  Card,
  CardAction,
  CardContent,
  CardFooter,
  CardHeader,
} from '@/components/ui/card';
import { Skeleton } from '@/components/ui/skeleton';
import PostBookmarkButton from '@/features/bookmark/components/PostBookmarkButton';
import { useSession } from '@/lib/auth/client';

interface PostContentProps {
  slug: string;
}

export default function PostContent({ slug }: PostContentProps) {
  const { data: session } = useSession();
  const isAuthenticated = session === undefined ? undefined : !!session?.user;
  const { data, isPending } = useGetPostBySlug(slug);

  if (isPending) {
    return <Skeleton className="h-96 w-full" />;
  }

  if (!data) {
    return null;
  }

  const post = data.data;

  return (
    <Card>
      <CardHeader>
        <div className="flex items-start justify-between">
          <div className="flex items-center space-x-3">
            <Avatar size="lg">
              <AvatarFallback />
            </Avatar>
            <div className="flex flex-col gap-2">{post.author.name}</div>
          </div>

          <CardAction>
            <div className="flex items-center gap-2">
              <Button variant="ghost" size="icon-sm">
                <DotsThreeIcon weight="bold" />
              </Button>
            </div>
          </CardAction>
        </div>
      </CardHeader>

      <CardContent className="space-y-6">
        <div className="space-y-3">
          <BaseViewer content={post.content} />
        </div>
      </CardContent>

      <CardFooter className="justify-between">
        <div className="flex items-center gap-2">
          <Button variant="ghost" size="sm" className="gap-1.5">
            <HeartIcon />
            {post.likeCount}
          </Button>

          <Button variant="ghost" size="sm" className="gap-1.5">
            <ChatCircleIcon />
            {post.commentCount}
          </Button>
        </div>

        <PostBookmarkButton
          postId={post.id}
          slug={post.slug}
          bookmarked={post.bookmarked}
          isAuthenticated={isAuthenticated}
          size="sm"
        />
      </CardFooter>
    </Card>
  );
}
