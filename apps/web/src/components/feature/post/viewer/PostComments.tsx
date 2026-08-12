'use client';

import {
  CheckCircleIcon,
  HeartIcon,
  PaperPlaneRightIcon,
  TrashIcon,
} from '@phosphor-icons/react';
import { useTranslations } from 'next-intl';
import { useMemo, useState } from 'react';
import { toast } from 'sonner';

import { useGetPostCommentsInfinite } from '@/api/__generated__/comment/comment';
import type { GetCommentsResponseCommentsNodesItemContent } from '@/api/__generated__/types';
import { useCommentAcceptance } from '@/components/feature/post/hooks/useCommentAcceptance';
import { useCommentDelete } from '@/components/feature/post/hooks/useCommentDelete';
import { useCommentLike } from '@/components/feature/post/hooks/useCommentLike';
import { useCreateComment } from '@/components/feature/post/hooks/useCreateComment';
import { ProfileAvatar } from '@/components/feature/profile/ProfileAvatar';
import { ProfileHoverCard } from '@/components/feature/profile/ProfileHoverCard';
import {
  AlertDialog,
  AlertDialogAction,
  AlertDialogCancel,
  AlertDialogContent,
  AlertDialogFooter,
  AlertDialogHeader,
  AlertDialogTitle,
} from '@/components/ui/alert-dialog';
import { Badge } from '@/components/ui/badge';
import { Button } from '@/components/ui/button';
import { Card } from '@/components/ui/card';
import { RelativeTime } from '@/components/ui/relative-time';
import { Separator } from '@/components/ui/separator';
import { Skeleton } from '@/components/ui/skeleton';
import { Textarea } from '@/components/ui/textarea';
import { useRequireAuth } from '@/hooks/use-require-auth';
import { useSession } from '@/lib/auth/client';
import SyncError, { ErrorCode } from '@/lib/error';
import { cn } from '@/lib/utils';

import { COMMENT_PAGE_SIZE } from '../constants';
import { PostType } from '../types/post';
import { COMMENT_COMPOSER_ID } from './utils/commentComposer';

interface PostCommentsProps {
  className?: string;
  slug: string;
  postId: number;
  postType: PostType;
  // 현재 보고 있는 사용자가 게시글 작성자인지 여부 — 질문 게시글에서만,
  // 그리고 작성자만 답변 채택/채택 취소를 할 수 있다.
  isPostAuthor: boolean;
  // 프로젝트 게시글은 공개 프로젝트라도 팀원만 댓글을 쓸 수 있다. 서버가 내려준
  // 이 값이 false 면 입력창 대신 안내 문구를 보여준다.
  canComment: boolean;
  // 프로젝트 게시글 여부. 비로그인 상태에서는 팀원인지 알 수 없으므로, 로그인
  // 안내와 함께 팀원만 댓글을 쓸 수 있다는 사실을 미리 알려 준다.
  requiresMembership: boolean;
}

function PostCommentItem({
  slug,
  postId,
  comment,
  showAcceptance,
  canManageAcceptance,
}: {
  slug: string;
  postId: number;
  comment: GetCommentsResponseCommentsNodesItemContent;
  // 질문 게시글에서만: 채택 상태 배지를 표시할지 여부.
  showAcceptance: boolean;
  // 질문 게시글의 작성자에게만: 채택/채택 취소 액션을 노출할지 여부.
  canManageAcceptance: boolean;
}) {
  const t = useTranslations('pages.posts.comments');
  const tDelete = useTranslations('pages.posts.comments.delete');
  const author = comment.author;
  const isAuthorDeleted = author?.isDeleted ?? false;
  const authorName = isAuthorDeleted
    ? t('deleted-author')
    : (author?.name ?? '?');
  const { acceptComment, unacceptComment, isPending } =
    useCommentAcceptance(slug);
  const { toggleLike } = useCommentLike(slug);
  const { deleteComment, isPending: isDeleting } = useCommentDelete(
    slug,
    postId,
  );
  const { requireAuth } = useRequireAuth();
  const [isDeleteDialogOpen, setIsDeleteDialogOpen] = useState(false);

  return (
    <div className="flex items-start gap-3 py-4">
      {isAuthorDeleted ? (
        <ProfileAvatar name={authorName} size="sm" />
      ) : (
        <ProfileHoverCard
          handle={author?.handle ?? ''}
          name={authorName}
          imageUrl={author?.profileImageUrl ?? undefined}
          size="sm"
        />
      )}

      <div className="flex min-w-0 flex-1 flex-col gap-1">
        <div className="flex flex-wrap items-center gap-2">
          <span
            className={cn(
              'text-sm font-semibold',
              isAuthorDeleted && 'text-muted-foreground italic',
            )}
          >
            {authorName}
          </span>
          {comment.isPostAuthor && (
            <Badge variant="secondary" className="text-[10px]">
              {t('author-badge')}
            </Badge>
          )}
          {showAcceptance && comment.isAccepted && !comment.isDeleted && (
            <Badge color="success" className="text-[10px]">
              <CheckCircleIcon weight="fill" />
              {t('accepted-badge')}
            </Badge>
          )}
          <span className="text-muted-foreground text-xs">
            <RelativeTime date={comment.createdAt} />
          </span>
        </div>

        <p className="text-sm break-words whitespace-pre-wrap">
          {comment.isDeleted ? (
            <span className="text-muted-foreground italic">{t('deleted')}</span>
          ) : (
            comment.content
          )}
        </p>

        {!comment.isDeleted && (
          <div className="flex items-center gap-1">
            <Button
              variant="ghost"
              size="sm"
              className="text-muted-foreground h-6 px-2 text-[11px]"
              aria-label={t('like-button')}
              onClick={() => {
                if (!requireAuth({ intent: 'like' })) {
                  return;
                }

                toggleLike(comment.id, comment.liked);
              }}
            >
              <HeartIcon
                className={cn(
                  comment.liked && 'fill-destructive text-destructive',
                )}
                weight={comment.liked ? 'fill' : 'regular'}
              />
              {comment.likeCount}
            </Button>

            {canManageAcceptance && (
              <Button
                variant="outline"
                size="sm"
                className="h-6 px-2 text-[11px]"
                disabled={isPending}
                onClick={() =>
                  comment.isAccepted
                    ? unacceptComment(String(comment.id))
                    : acceptComment(String(comment.id))
                }
              >
                <CheckCircleIcon
                  weight={comment.isAccepted ? 'fill' : undefined}
                />
                {comment.isAccepted ? t('unaccept-button') : t('accept-button')}
              </Button>
            )}

            {comment.canDelete && (
              <Button
                variant="ghost"
                size="sm"
                className="text-muted-foreground hover:text-destructive h-6 px-2 text-[11px]"
                aria-label={tDelete('button')}
                onClick={() => setIsDeleteDialogOpen(true)}
              >
                <TrashIcon />
              </Button>
            )}
          </div>
        )}
      </div>

      <AlertDialog
        open={isDeleteDialogOpen}
        onOpenChange={setIsDeleteDialogOpen}
      >
        <AlertDialogContent>
          <AlertDialogHeader>
            <AlertDialogTitle>{tDelete('title')}</AlertDialogTitle>
          </AlertDialogHeader>

          <AlertDialogFooter>
            <AlertDialogCancel>{tDelete('actions.cancel')}</AlertDialogCancel>
            <AlertDialogAction
              variant="destructive"
              disabled={isDeleting}
              onClick={() => {
                deleteComment(comment.id, {
                  onSuccess: () => {
                    toast.success(tDelete('messages.success'));
                    setIsDeleteDialogOpen(false);
                  },
                  onError: (error) => {
                    if (
                      error instanceof SyncError &&
                      error.code === ErrorCode.COMMENT_NOT_FOUND
                    ) {
                      toast.error(tDelete('messages.not-found'));
                      return;
                    }

                    toast.error(tDelete('messages.error'));
                  },
                });
              }}
            >
              {tDelete('actions.confirm')}
            </AlertDialogAction>
          </AlertDialogFooter>
        </AlertDialogContent>
      </AlertDialog>
    </div>
  );
}

export default function PostComments({
  className,
  slug,
  postId,
  postType,
  isPostAuthor,
  canComment,
  requiresMembership,
}: PostCommentsProps) {
  const t = useTranslations('pages.posts.comments');
  const { data: session, isPending: isSessionPending } = useSession();
  const { requireAuth } = useRequireAuth();

  const showAcceptance = postType === PostType.QUESTION;
  const canManageAcceptance = showAcceptance && isPostAuthor;

  const { data, fetchNextPage, hasNextPage, isFetchingNextPage, isPending } =
    useGetPostCommentsInfinite(
      slug,
      { first: COMMENT_PAGE_SIZE },
      {
        query: {
          getNextPageParam: (lastPage) => {
            const pageInfo = lastPage.data.comments?.pageInfo;
            return pageInfo?.hasNextPage
              ? (pageInfo.endCursor ?? undefined)
              : undefined;
          },
        },
      },
    );
  const [draft, setDraft] = useState('');

  const { mutate: createComment, isPending: isSubmitting } =
    useCreateComment(postId);

  const comments = useMemo(() => {
    return (
      data?.pages.flatMap(
        (page) => page.data.comments?.nodes.map((node) => node.content) ?? [],
      ) ?? []
    );
  }, [data]);

  function handleSubmit() {
    if (!requireAuth({ intent: 'comment' })) {
      return;
    }

    const content = draft.trim();
    if (!content) {
      return;
    }

    createComment(
      { slug, data: { content } },
      {
        onSuccess: () => setDraft(''),
        onError: (error) => {
          if (error instanceof SyncError) {
            switch (error.code) {
              case ErrorCode.POST_NOT_FOUND:
                toast.error(t('composer.messages.not-found'));
                return;
              case ErrorCode.COMMENT_NOT_ALLOWED:
                toast.error(t('composer.messages.not-allowed'));
                return;
            }
          }
          toast.error(t('composer.messages.error'));
        },
      },
    );
  }

  return (
    <Card className={cn('gap-0 p-0 lg:max-h-[calc(100svh-7.5rem)]', className)}>
      <div className="flex items-center justify-between px-5 py-4">
        <h2 className="text-sm font-semibold">
          {t('title')} {comments.length > 0 && `· ${comments.length}`}
        </h2>
      </div>

      <Separator />

      {!isSessionPending && session?.user && canComment && (
        <>
          <div className="flex items-start gap-3 px-5 py-4">
            <ProfileAvatar
              name={session.user.name}
              imageUrl={session.user.image}
              size="sm"
            />

            <div className="flex flex-1 flex-col gap-2">
              <Textarea
                id={COMMENT_COMPOSER_ID}
                value={draft}
                onChange={(event) => setDraft(event.target.value)}
                placeholder={t('composer.placeholder')}
                className="min-h-16"
              />

              <div className="flex justify-end">
                <Button
                  size="sm"
                  disabled={!draft.trim() || isSubmitting}
                  onClick={handleSubmit}
                >
                  <PaperPlaneRightIcon />
                  {t('composer.submit')}
                </Button>
              </div>
            </div>
          </div>

          <Separator />
        </>
      )}

      {!isSessionPending && !session?.user && (
        <>
          <div className="flex flex-col gap-2 px-5 py-4">
            {requiresMembership && (
              <p className="text-muted-foreground text-sm">
                {t('composer.members-only')}
              </p>
            )}

            <Button
              variant="outline"
              className="w-full justify-center"
              onClick={() => {
                requireAuth({ intent: 'comment' });
              }}
            >
              {t('composer.login')}
            </Button>
          </div>

          <Separator />
        </>
      )}

      {!isSessionPending && session?.user && !canComment && (
        <>
          <p className="text-muted-foreground px-5 py-4 text-sm">
            {t('composer.members-only')}
          </p>

          <Separator />
        </>
      )}

      <div className="divide-border/60 divide-y px-5 lg:min-h-0 lg:flex-1 lg:overflow-y-auto">
        {isPending && (
          <div className="space-y-4 py-4">
            <Skeleton className="h-12 w-full" />
            <Skeleton className="h-12 w-full" />
          </div>
        )}

        {!isPending && comments.length === 0 && (
          <p className="text-muted-foreground py-6 text-center text-sm">
            {t('empty')}
          </p>
        )}

        {comments.map((comment) => (
          <PostCommentItem
            key={comment.id}
            slug={slug}
            postId={postId}
            comment={comment}
            showAcceptance={showAcceptance}
            canManageAcceptance={canManageAcceptance}
          />
        ))}

        {hasNextPage && (
          <div className="py-4">
            <Button
              variant="outline"
              className="w-full justify-center"
              disabled={isFetchingNextPage}
              onClick={() => {
                fetchNextPage();
              }}
            >
              {isFetchingNextPage ? t('loading-more') : t('load-more')}
            </Button>
          </div>
        )}
      </div>
    </Card>
  );
}
