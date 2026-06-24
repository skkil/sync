'use client';

import { DotsThreeIcon } from '@phosphor-icons/react';
import { EditorContent, useEditor } from '@tiptap/react';
import StarterKit from '@tiptap/starter-kit';

import type { GetPostResponse } from '@/api/__generated__/types';
import { ProfileHoverCard } from '@/components/feature/profile/ProfileHoverCard';
import { Badge } from '@/components/ui/badge';
import { Button } from '@/components/ui/button';
import { Card, CardContent, CardHeader } from '@/components/ui/card';
import {
  DropdownMenu,
  DropdownMenuContent,
  DropdownMenuItem,
  DropdownMenuTrigger,
} from '@/components/ui/dropdown-menu';

import { ImageNode } from '../editor/extensions/nodes/image';
import { deserialize } from '../editor/utils/serializer';
import { PostCardActions } from './components/PostCardActions';

interface PostPreviewProps {
  id: number;
  author: GetPostResponse['author'];
  project?: GetPostResponse['project'];
  content: GetPostResponse['content'];
  likeCount: number;
  commentCount: number;
  bookmarked: boolean;
}

export default function PostPreview({
  id,
  author,
  project,
  content,
  likeCount,
  commentCount,
  bookmarked,
}: PostPreviewProps) {
  const editor = useEditor({
    extensions: [StarterKit, ImageNode],
    content: deserialize(content.json, content.media),
    editable: false,
    immediatelyRender: false,
  });

  return (
    <Card>
      <CardHeader>
        <PostPreviewHeader author={author} project={project} />
      </CardHeader>

      <CardContent className="space-y-4">
        <EditorContent editor={editor} />
        <PostCardActions
          postId={id}
          likeCount={likeCount}
          commentCount={commentCount}
          bookmarked={bookmarked}
        />
      </CardContent>
    </Card>
  );
}

function PostPreviewHeader({
  author,
  project,
}: {
  author: GetPostResponse['author'];
  project?: GetPostResponse['project'];
}) {
  return (
    <div className="flex items-start justify-between">
      <div className="flex items-center gap-2">
        <ProfileHoverCard
          handle={author.name}
          name={author.name}
          size="default"
        />

        <div className="flex flex-col">
          <span className="text-sm font-semibold">{author.name}</span>
          <span className="text-muted-foreground text-xs">
            @{author.name} · 2h
          </span>
        </div>

        {project?.name && <Badge variant="secondary">{project.name}</Badge>}
      </div>

      <DropdownMenu>
        <DropdownMenuTrigger asChild>
          <Button variant="ghost" size="icon-sm" aria-label="Post options">
            <DotsThreeIcon weight="bold" />
          </Button>
        </DropdownMenuTrigger>

        <DropdownMenuContent align="end">
          <DropdownMenuItem>Copy link</DropdownMenuItem>
          <DropdownMenuItem>Report</DropdownMenuItem>
          <DropdownMenuItem variant="destructive">Delete</DropdownMenuItem>
        </DropdownMenuContent>
      </DropdownMenu>
    </div>
  );
}
