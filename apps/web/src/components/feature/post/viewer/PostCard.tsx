'use client';

import { CheckCircleIcon, ClockIcon } from '@phosphor-icons/react';
import type { Editor } from '@tiptap/react';
import { useTranslations } from 'next-intl';
import { useRouter } from 'next/navigation';

import { useGetPostBySlug } from '@/api/__generated__/post/post';
import { Card, CardContent, CardHeader } from '@/components/ui/card';
import { Skeleton } from '@/components/ui/skeleton';
import ROUTES from '@/util/routes';

import { PostType } from '../types/post';
import { PostBody } from './components/PostBody';
import { PostCardActions } from './components/PostCardActions';
import { PostViewHeader } from './components/PostViewHeader';
import { PreviewTagChips } from './components/PreviewTagChips';
import PostRenderErrorBoundary from './error/PostRenderErrorBoundary';
import { useReadOnlyPostEditor } from './hooks/useReadOnlyPostEditor';
import {
  type PostProjectSummary,
  type PostSummary,
  type PostViewSource,
  toPostViewSource,
} from './types';
import { normalizePostContent } from './utils/normalizePostContent';
import {
  type ReviewStatus,
  getMockReviewStatus,
  getMockTags,
} from './utils/placeholderData';

const WORDS_PER_MINUTE = 200;

// ---- PostCard: detail view, loads its own data by slug ----

interface PostCardProps {
  slug: string;
}

export function PostCard({ slug }: PostCardProps) {
  const { data, isPending } = useGetPostBySlug(slug);

  if (isPending || !data) {
    return <Skeleton className="h-40 w-full" />;
  }

  return (
    <PostRenderErrorBoundary>
      <PostCardBySource source={toPostViewSource(data.data)} />
    </PostRenderErrorBoundary>
  );
}

function PostCardBySource({ source }: { source: PostViewSource }) {
  const { summary, content } = source;
  const editor = useReadOnlyPostEditor(normalizePostContent(content));
  const postPath = summary.project?.handle
    ? ROUTES.PROJECT_POST(summary.project.handle, summary.slug)
    : ROUTES.POST(summary.slug);

  const typeCardProps: TypePostCardProps = { summary, editor, postPath };

  switch (summary.type) {
    case PostType.QUESTION:
      return <QuestionTypePostCard {...typeCardProps} />;
    case PostType.LONG:
      return <LongTypePostCard {...typeCardProps} />;
    case PostType.SHORT:
    default:
      return <ShortTypePostCard {...typeCardProps} />;
  }
}

interface TypePostCardProps {
  summary: PostSummary;
  editor: Editor | null;
  postPath: string;
}

function ShortTypePostCard({ summary, editor, postPath }: TypePostCardProps) {
  return (
    <Card>
      <CardHeader>
        <PostViewHeader
          summary={summary}
          postPath={postPath}
          variant="detail"
        />
      </CardHeader>

      <CardContent className="space-y-3">
        {summary.title && (
          <h3 className="text-lg font-semibold">{summary.title}</h3>
        )}
        <PostBody editor={editor} />
        <PostCardActions
          postId={summary.id}
          liked={summary.liked}
          likeCount={summary.likeCount}
          commentCount={summary.commentCount}
          bookmarked={summary.bookmarked}
        />
      </CardContent>
    </Card>
  );
}

function LongTypePostCard({ summary, editor, postPath }: TypePostCardProps) {
  return (
    <Card>
      <CardHeader>
        <PostViewHeader
          summary={summary}
          postPath={postPath}
          variant="detail"
        />
      </CardHeader>

      <CardContent className="space-y-4">
        {summary.title && (
          <h3 className="text-lg font-semibold">{summary.title}</h3>
        )}
        <PostBody editor={editor} className="line-clamp-4" />
        <PostCardActions
          postId={summary.id}
          liked={summary.liked}
          likeCount={summary.likeCount}
          commentCount={summary.commentCount}
          bookmarked={summary.bookmarked}
        />
      </CardContent>
    </Card>
  );
}

function QuestionTypePostCard({
  summary,
  editor,
  postPath,
}: TypePostCardProps) {
  return (
    <Card>
      <CardHeader>
        <PostViewHeader
          summary={summary}
          postPath={postPath}
          variant="detail"
        />
      </CardHeader>

      <CardContent className="space-y-4">
        {summary.title && (
          <h3 className="text-lg font-semibold">{summary.title}</h3>
        )}
        <PostBody editor={editor} />
        <PostCardActions
          postId={summary.id}
          liked={summary.liked}
          likeCount={summary.likeCount}
          commentCount={summary.commentCount}
          bookmarked={summary.bookmarked}
        />
      </CardContent>
    </Card>
  );
}

// ---- PostPreviewCard: feed view, data injected by PostList ----

export interface PostPreviewCardProps {
  source: PostViewSource;
}

export function PostPreviewCard({ source }: PostPreviewCardProps) {
  return (
    <PostRenderErrorBoundary>
      <PostPreviewCardBySource source={source} />
    </PostRenderErrorBoundary>
  );
}

function PostPreviewCardBySource({ source }: PostPreviewCardProps) {
  const { summary, content } = source;
  const router = useRouter();
  const editor = useReadOnlyPostEditor(normalizePostContent(content));

  const postPath = summary.project?.handle
    ? ROUTES.PROJECT_POST(summary.project.handle, summary.slug)
    : ROUTES.POST(summary.slug);

  const typePreviewCardProps: TypePostPreviewCardProps = {
    summary,
    editor,
    postPath,
    onClick: () => router.push(postPath),
    reviewStatus: getMockReviewStatus(summary.id),
    tags: getMockTags(summary.id),
  };

  switch (summary.type) {
    case PostType.QUESTION:
      return <QuestionTypePostPreviewCard {...typePreviewCardProps} />;
    case PostType.LONG:
      return <LongTypePostPreviewCard {...typePreviewCardProps} />;
    case PostType.SHORT:
    default:
      return <ShortTypePostPreviewCard {...typePreviewCardProps} />;
  }
}

interface TypePostPreviewCardProps {
  summary: PostSummary;
  editor: Editor | null;
  postPath: string;
  onClick: () => void;
  reviewStatus: ReviewStatus;
  tags: string[];
}

function ReviewStatusIndicator({ status }: { status: ReviewStatus }) {
  const t = useTranslations('components.post.viewer');

  if (status === 'verified') {
    return (
      <span className="flex items-center gap-1 text-xs font-medium text-success-text">
        <CheckCircleIcon weight="fill" />
        {t('reviewStatus.verified')}
      </span>
    );
  }

  if (status === 'verify-soon') {
    return (
      <span className="flex items-center gap-1 text-xs font-medium text-warning-text">
        <ClockIcon />
        {t('reviewStatus.verify-soon')}
      </span>
    );
  }

  return null;
}

function ShortTypePostPreviewCard({
  summary,
  editor,
  postPath,
  onClick,
  reviewStatus,
}: TypePostPreviewCardProps) {
  return (
    <Card onClick={onClick}>
      <CardHeader>
        <PostViewHeader
          summary={summary}
          postPath={postPath}
          variant="preview"
        />
      </CardHeader>

      <CardContent className="space-y-4">
        {summary.title && (
          <h3 className="text-lg font-semibold">{summary.title}</h3>
        )}
        <PostBody editor={editor} />

        <div className="flex items-center justify-between">
          <ReviewStatusIndicator status={reviewStatus} />
          <PostCardActions
            postId={summary.id}
            liked={summary.liked}
            likeCount={summary.likeCount}
            commentCount={summary.commentCount}
            bookmarked={summary.bookmarked}
          />
        </div>
      </CardContent>
    </Card>
  );
}

function LongTypePostPreviewCard({
  summary,
  editor,
  postPath,
  onClick,
  reviewStatus,
  tags,
}: TypePostPreviewCardProps) {
  return (
    <Card onClick={onClick}>
      <CardHeader>
        <PostViewHeader
          summary={summary}
          postPath={postPath}
          variant="preview"
        />
      </CardHeader>

      <CardContent className="space-y-4">
        <ArticlePreviewMedia editor={editor} project={summary.project} />

        {summary.title && (
          <h3 className="text-lg font-semibold">{summary.title}</h3>
        )}
        <PostBody editor={editor} className="line-clamp-6" />

        <div className="flex items-center justify-between">
          <ReviewStatusIndicator status={reviewStatus} />
          <PostCardActions
            postId={summary.id}
            liked={summary.liked}
            likeCount={summary.likeCount}
            commentCount={summary.commentCount}
            bookmarked={summary.bookmarked}
          />
        </div>

        <PreviewTagChips tags={tags} />
      </CardContent>
    </Card>
  );
}

function QuestionTypePostPreviewCard({
  summary,
  editor,
  postPath,
  onClick,
  tags,
}: TypePostPreviewCardProps) {
  const t = useTranslations('components.post.viewer');

  return (
    <Card onClick={onClick}>
      <CardContent className="flex gap-4">
        <div className="flex w-14 shrink-0 flex-col items-center gap-2 text-center">
          <div>
            <p className="text-lg font-semibold">{summary.likeCount}</p>
            <p className="text-muted-foreground text-xs">{t('votes')}</p>
          </div>
          <div className="rounded-md border px-2 py-1">
            <p className="text-sm font-semibold">{summary.commentCount}</p>
            <p className="text-muted-foreground text-xs">{t('answers')}</p>
          </div>
        </div>

        <div className="min-w-0 flex-1 space-y-3">
          <PostViewHeader
            summary={summary}
            postPath={postPath}
            variant="preview"
          />

          {summary.resolved && (
            <span className="text-xs font-medium text-success-text">
              {t('answered')}
            </span>
          )}

          {summary.title && (
            <h3 className="text-lg font-semibold">{summary.title}</h3>
          )}
          <PostBody editor={editor} />

          <div className="flex items-center justify-between">
            <PostCardActions
              postId={summary.id}
              liked={summary.liked}
              likeCount={summary.likeCount}
              commentCount={summary.commentCount}
              bookmarked={summary.bookmarked}
            />
            <PreviewTagChips tags={tags} />
          </div>
        </div>
      </CardContent>
    </Card>
  );
}

function ArticlePreviewMedia({
  editor,
  project,
}: {
  editor: Editor | null;
  project?: PostProjectSummary;
}) {
  const t = useTranslations('components.post.viewer');

  // TODO: real reading time needs a stable word count from the server —
  // this estimates from the loaded editor content client-side.
  const wordCount = editor?.getText().split(/\s+/).filter(Boolean).length ?? 0;
  const readingMinutes = Math.max(1, Math.ceil(wordCount / WORDS_PER_MINUTE));
  // TODO: no thumbnail/category field on posts yet — falls back to the
  // project name, or a generic label.
  const category = project?.name?.toUpperCase() ?? t('article');

  return (
    <div className="relative flex h-32 items-end rounded-lg bg-gradient-to-br from-primary/20 to-success-tint p-4">
      <span className="absolute top-3 right-3 rounded-full bg-background/80 px-2 py-0.5 text-xs font-medium">
        {t('minRead', { minutes: readingMinutes })}
      </span>
      <span className="text-xs font-semibold tracking-wide text-primary">
        {category}
      </span>
    </div>
  );
}
