'use client';

import {
  ChatCircleIcon,
  DotsThreeIcon,
  HeartIcon,
} from '@phosphor-icons/react';

import { useGetReflectionBySlug } from '@/api/__generated__/reflection/reflection';
import { Avatar, AvatarFallback } from '@/components/ui/avatar';
import { Button } from '@/components/ui/button';
import {
  Card,
  CardAction,
  CardContent,
  CardFooter,
  CardHeader,
} from '@/components/ui/card';
import { BaseViewer } from '@/components/ui/editor';
import { Skeleton } from '@/components/ui/skeleton';

interface PostContentProps {
  slug: string;
}

export default function PostContent({ slug }: PostContentProps) {
  const { data, isPending } = useGetReflectionBySlug(slug);

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
      </CardFooter>
    </Card>
  );
}
