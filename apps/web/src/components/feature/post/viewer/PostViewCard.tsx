'use client';

import { useTranslations } from 'next-intl';
import { useRouter } from 'next/navigation';

import type { GetPostResponse } from '@/api/__generated__/types';
import { ProfileHoverCard } from '@/components/feature/profile/ProfileHoverCard';
import { Badge } from '@/components/ui/badge';
import { Card, CardContent, CardHeader } from '@/components/ui/card';
import { RelativeTime } from '@/components/ui/relative-time';
import ROUTES from '@/util/routes';

import { PostStatus, PostType } from '../types/post';
import PostErrorBoundary from './PostErrorBoundary';
import { PostCardActions } from './components/PostCardActions';
import { PostOverflowMenu } from './components/PostOverflowMenu';
import { PostTypeBadge } from './components/PostTypeBadge';
import { useReadOnlyPostEditor } from './hooks/useReadOnlyPostEditor';
import type { PostAuthorSummary, PostProjectSummary } from './types';
import {
  PostBody,
  type PostCardVariant,
  getPostCardStyles,
} from './variants/PostBody';

export interface PostViewCardProps {
  id: number;
  slug: string;
  type?: PostType;
  status?: PostStatus;
  title?: string | null;
  author: PostAuthorSummary;
  project?: PostProjectSummary;
  content: GetPostResponse['content'];
  liked: boolean;
  likeCount: number;
  commentCount: number;
  bookmarked: boolean;
  isAuthor: boolean;
  createdAt: string;
  variant: PostCardVariant;
}

export default function PostViewCard(props: PostViewCardProps) {
  return (
    <PostErrorBoundary>
      <PostViewCardContent {...props} />
    </PostErrorBoundary>
  );
}

function PostViewCardContent({
  id,
  slug,
  type,
  status,
  title,
  author,
  project,
  content,
  liked,
  likeCount,
  commentCount,
  bookmarked,
  isAuthor,
  createdAt,
  variant,
}: PostViewCardProps) {
  const router = useRouter();
  const editor = useReadOnlyPostEditor(content);
  const { contentClassName, bodyClassName } = getPostCardStyles(variant, type);

  const postPath = project?.handle
    ? ROUTES.PROJECT_POST(project.handle, slug)
    : ROUTES.POST(slug);

  const handleClickCard =
    variant === 'preview' ? () => router.push(postPath) : undefined;

  return (
    <Card onClick={handleClickCard}>
      <CardHeader>
        <PostViewCardHeader
          postId={id}
          slug={slug}
          postPath={postPath}
          type={type}
          status={status}
          author={author}
          project={project}
          isAuthor={isAuthor}
          createdAt={createdAt}
          variant={variant}
        />
      </CardHeader>

      <CardContent className={contentClassName}>
        {title && <h3 className="text-lg font-semibold">{title}</h3>}
        <PostBody type={type} editor={editor} className={bodyClassName} />
        <PostCardActions
          postId={id}
          liked={liked}
          likeCount={likeCount}
          commentCount={commentCount}
          bookmarked={bookmarked}
        />
      </CardContent>
    </Card>
  );
}

function PostViewCardHeader({
  postId,
  slug,
  postPath,
  type,
  status,
  author,
  project,
  isAuthor,
  createdAt,
  variant,
}: {
  postId: number;
  slug: string;
  postPath: string;
  type?: PostType;
  status?: PostStatus;
  author: PostAuthorSummary;
  project?: PostProjectSummary;
  isAuthor: boolean;
  createdAt: string;
  variant: PostCardVariant;
}) {
  const tPost = useTranslations('components.post');

  const isPreview = variant === 'preview';
  const stopPropagation = isPreview
    ? (event: React.MouseEvent) => event.stopPropagation()
    : undefined;
  const redirectAfterDelete =
    variant === 'detail'
      ? project?.handle
        ? ROUTES.PROJECT(project.handle)
        : ROUTES.HOME()
      : undefined;

  return (
    <div className="flex items-start justify-between">
      <div className="flex items-center gap-2">
        <div onClick={stopPropagation}>
          <ProfileHoverCard
            handle={author.handle}
            name={author.name}
            size={isPreview ? 'default' : 'sm'}
          />
        </div>

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

        {project?.name && <Badge variant="secondary">{project.name}</Badge>}
      </div>

      <PostOverflowMenu
        postId={postId}
        slug={slug}
        postPath={postPath}
        isAuthor={isAuthor}
        projectHandle={project?.handle}
        redirectAfterDelete={redirectAfterDelete}
      />
    </div>
  );
}
