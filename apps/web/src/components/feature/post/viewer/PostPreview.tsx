'use client';

import { useEditor } from '@tiptap/react';
import StarterKit from '@tiptap/starter-kit';
import { useTranslations } from 'next-intl';
import { useRouter } from 'next/navigation';

import { useGetProjectByHandle } from '@/api/__generated__/project/project';
import type { GetPostResponse } from '@/api/__generated__/types';
import { ProfileHoverCard } from '@/components/feature/profile/ProfileHoverCard';
import { Badge } from '@/components/ui/badge';
import { Card, CardContent, CardHeader } from '@/components/ui/card';
import { RelativeTime } from '@/components/ui/relative-time';
import ROUTES from '@/util/routes';

import { ImageNode } from '../editor/extensions/nodes/image';
import { deserialize } from '../editor/utils/serializer';
import {
  PostScope,
  PostStatus,
  PostType,
  isPublicPublishedPost,
} from '../types/post';
import { PostCardActions } from './components/PostCardActions';
import { PostOverflowMenu } from './components/PostOverflowMenu';
import { PostTypeBadge } from './components/PostTypeBadge';
import { PostBody } from './variants/PostBody';

interface PostPreviewProps {
  id: number;
  slug: string;
  type?: PostType;
  scope?: PostScope;
  status?: PostStatus;
  title?: string | null;
  author: GetPostResponse['author'];
  project?: string;
  content: GetPostResponse['content'];
  likeCount: number;
  commentCount: number;
  bookmarked: boolean;
  createdAt: string;
}

export default function PostPreview({
  id,
  slug,
  type,
  scope,
  status,
  title,
  author,
  project,
  content,
  likeCount,
  commentCount,
  bookmarked,
  createdAt,
}: PostPreviewProps) {
  const router = useRouter();
  const editor = useEditor({
    extensions: [StarterKit, ImageNode],
    content: deserialize(content.json, content.media),
    editable: false,
    immediatelyRender: false,
  });

  const showActions = isPublicPublishedPost(scope, status);

  const handleClickCard = () => {
    if (project) {
      router.push(ROUTES.PROJECT_POST(project, slug));
    } else {
      router.push(ROUTES.POST(slug));
    }
  };

  return (
    <Card onClick={handleClickCard}>
      <CardHeader>
        <PostPreviewHeader
          postId={id}
          slug={slug}
          type={type}
          scope={scope}
          status={status}
          author={author}
          project={project}
          createdAt={createdAt}
        />
      </CardHeader>

      <CardContent className="space-y-4">
        {title && type !== PostType.SHORT && (
          <h2 className="line-clamp-2 text-xl font-semibold leading-tight">
            {title}
          </h2>
        )}
        <PostBody
          type={type}
          editor={editor}
          className={type === PostType.LONG ? 'line-clamp-6' : undefined}
        />
        {showActions && (
          <PostCardActions
            postId={id}
            likeCount={likeCount}
            commentCount={commentCount}
            bookmarked={bookmarked}
          />
        )}
      </CardContent>
    </Card>
  );
}

function PostPreviewHeader({
  postId,
  slug,
  type,
  scope,
  status,
  author,
  project,
  createdAt,
}: {
  postId: number;
  slug: string;
  type?: PostType;
  scope?: PostScope;
  status?: PostStatus;
  author: GetPostResponse['author'];
  project?: string;
  createdAt: string;
}) {
  const { data: projectData } = useGetProjectByHandle(project ?? '', {
    query: {
      enabled: !!project,
    },
  });
  const tPost = useTranslations('components.post');

  return (
    <div className="flex items-start justify-between">
      <div className="flex flex-wrap items-center gap-2">
        <ProfileHoverCard
          handle={author.handle}
          name={author.name}
          size="default"
        />

        <div className="flex flex-col">
          <span className="text-sm font-semibold">{author.name}</span>
          <span className="text-muted-foreground text-xs">
            @{author.handle} · <RelativeTime date={createdAt} />
          </span>
        </div>

        {type && <PostTypeBadge type={type} />}

        {status === PostStatus.DRAFT && (
          <Badge variant="outline">{tPost('status.DRAFT')}</Badge>
        )}

        {scope === PostScope.WORKSPACE && (
          <Badge variant="outline">{tPost('scope.WORKSPACE')}</Badge>
        )}

        {projectData?.data && (
          <Badge variant="secondary">{projectData.data.name}</Badge>
        )}
      </div>

      <PostOverflowMenu
        postId={postId}
        slug={slug}
        authorHandle={author.handle}
        projectHandle={project}
      />
    </div>
  );
}
