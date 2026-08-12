import type { PostType } from '../types/post';
import PostComments from './PostComments';
import { PostSeriesCard } from './PostSeriesCard';
import { PostTableOfContents } from './PostTableOfContents';

interface PostViewerSidebarProps {
  slug: string;
  commentsEnabled: boolean;
  postId?: number;
  postType?: PostType;
  isPostAuthor: boolean;
  canComment: boolean;
  requiresMembership: boolean;
}

export function PostViewerSidebar({
  slug,
  commentsEnabled,
  postId,
  postType,
  isPostAuthor,
  canComment,
  requiresMembership,
}: PostViewerSidebarProps) {
  const showComments =
    commentsEnabled && postType !== undefined && postId !== undefined;

  return (
    <div
      className={
        showComments
          ? 'flex flex-col gap-6 lg:max-h-[calc(100svh-7.5rem)]'
          : 'flex flex-col gap-6'
      }
    >
      <div
        className={
          showComments
            ? 'flex flex-col gap-6 empty:hidden lg:min-h-0 lg:shrink lg:overflow-y-auto lg:overscroll-contain lg:px-1 lg:[scrollbar-gutter:stable]'
            : 'flex flex-col gap-6 empty:hidden'
        }
      >
        <PostTableOfContents />
        <PostSeriesCard slug={slug} />
      </div>

      {showComments && (
        <PostComments
          slug={slug}
          postId={postId}
          postType={postType}
          isPostAuthor={isPostAuthor}
          canComment={canComment}
          requiresMembership={requiresMembership}
          className="lg:max-h-[45svh] lg:shrink-0"
        />
      )}
    </div>
  );
}
