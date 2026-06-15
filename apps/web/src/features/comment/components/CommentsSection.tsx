'use client';

import {
  ChatCircleIcon,
  PencilSimpleIcon,
  TrashIcon,
} from '@phosphor-icons/react';
import { useQueryClient } from '@tanstack/react-query';
import { formatDistanceToNow } from 'date-fns';
import { useState } from 'react';
import { toast } from 'sonner';

import {
  getGetPostCommentsQueryOptions,
  useCreateComment,
  useDeleteComment,
  useGetPostComments,
  useUpdateComment,
} from '@/api/__generated__/comment/comment';
import type { GetCommentsResponseCommentsItem } from '@/api/__generated__/types';
import { Avatar, AvatarFallback } from '@/components/ui/avatar';
import { Button } from '@/components/ui/button';
import { Spinner } from '@/components/ui/spinner';
import { Textarea } from '@/components/ui/textarea';
import { useSession } from '@/lib/auth/client';
import { cn } from '@/lib/utils';

interface CommentsSectionProps {
  postId: string;
}

export default function CommentsSection({
  postId,
}: CommentsSectionProps) {
  const [isOpen, setIsOpen] = useState(false);
  const { data, isPending } = useGetPostComments(postId, {
    query: {
      enabled: isOpen,
    },
  });

  const comments = data?.data.comments ?? [];

  return (
    <div className="border-t pt-3">
      <Button
        type="button"
        variant="ghost"
        size="sm"
        className="px-2"
        onClick={() => setIsOpen((value) => !value)}
      >
        <ChatCircleIcon />
        <span>댓글</span>
      </Button>

      {isOpen && (
        <div className="mt-3 space-y-4">
          <CommentForm postId={postId} />

          {isPending ? (
            <div className="flex justify-center py-4">
              <Spinner />
            </div>
          ) : (
            <div className="space-y-4">
              {comments.map((comment) => (
                <CommentThread
                  key={comment.id}
                  comment={comment}
                  postId={postId}
                />
              ))}
            </div>
          )}
        </div>
      )}
    </div>
  );
}

function CommentThread({
  comment,
  postId,
}: {
  comment: GetCommentsResponseCommentsItem;
  postId: string;
}) {
  return (
    <div className="space-y-3">
      <CommentRow comment={comment} postId={postId} />

      {comment.replies.length > 0 && (
        <div className="ml-10 space-y-3 border-l pl-3">
          {comment.replies.map((reply) => (
            <CommentRow
              key={(reply as GetCommentsResponseCommentsItem).id}
              comment={reply as GetCommentsResponseCommentsItem}
              postId={postId}
              parentId={comment.id}
              isReply
            />
          ))}
        </div>
      )}
    </div>
  );
}

function CommentRow({
  comment,
  postId,
  parentId,
  isReply = false,
}: {
  comment: GetCommentsResponseCommentsItem;
  postId: string;
  parentId?: number;
  isReply?: boolean;
}) {
  const { data: session } = useSession();
  const [isReplying, setIsReplying] = useState(false);
  const [isEditing, setIsEditing] = useState(false);
  const isMine = session?.user.id === comment.author?.id?.toString();
  const authorHandle = comment.author?.handle;
  const authorLabel = authorHandle
    ? `@${authorHandle}`
    : `#${comment.author?.id ?? '?'}`;

  return (
    <div className={cn('flex gap-3', isReply && 'text-sm')}>
      <Avatar className="size-8">
        <AvatarFallback>{authorHandle?.[0] ?? '?'}</AvatarFallback>
      </Avatar>

      <div className="min-w-0 flex-1 space-y-2">
        <div className="rounded-lg bg-muted/40 px-3 py-2">
          <div className="flex items-center justify-between gap-2">
            <div className="min-w-0">
              <p className="truncate text-sm font-medium">{authorLabel}</p>
              <p className="text-xs text-muted-foreground">
                {formatDistanceToNow(new Date(comment.createdAt), {
                  addSuffix: true,
                })}
              </p>
            </div>

            {isMine && !comment.isDeleted && (
              <div className="flex shrink-0 items-center gap-1">
                <Button
                  type="button"
                  variant="ghost"
                  size="icon-xs"
                  onClick={() => setIsEditing((value) => !value)}
                >
                  <PencilSimpleIcon />
                </Button>
                <DeleteCommentButton commentId={comment.id} />
              </div>
            )}
          </div>

          {isEditing ? (
            <EditCommentForm
              commentId={comment.id}
              postId={postId}
              initialContent={comment.content ?? ''}
              onDone={() => setIsEditing(false)}
            />
          ) : (
            <p className="mt-2 whitespace-pre-wrap break-words text-sm">
              {comment.isDeleted ? '삭제된 댓글입니다.' : comment.content}
            </p>
          )}
        </div>

        {!isReply && !comment.isDeleted && session && (
          <div>
            <Button
              type="button"
              variant="ghost"
              size="xs"
              onClick={() => setIsReplying((value) => !value)}
            >
              답글
            </Button>

            {isReplying && (
              <div className="mt-2">
                <CommentForm
                  postId={postId}
                  parentId={parentId ?? comment.id}
                  onSuccess={() => setIsReplying(false)}
                />
              </div>
            )}
          </div>
        )}
      </div>
    </div>
  );
}

function CommentForm({
  postId,
  parentId,
  onSuccess,
}: {
  postId: string;
  parentId?: number;
  onSuccess?: () => void;
}) {
  const queryClient = useQueryClient();
  const { data: session } = useSession();
  const [content, setContent] = useState('');

  const { mutate, isPending } = useCreateComment({
    mutation: {
      onSuccess: () => {
        setContent('');
        queryClient.invalidateQueries(
          getGetPostCommentsQueryOptions(postId),
        );
        onSuccess?.();
      },
      onError: () => toast.error('댓글을 저장하지 못했습니다.'),
    },
  });

  if (!session) {
    return null;
  }

  const handleSubmit = () => {
    const trimmed = content.trim();
    if (!trimmed) {
      return;
    }

    mutate({
      postId,
      data: {
        parentId,
        content: trimmed,
      },
    });
  };

  return (
    <div className="space-y-2">
      <Textarea
        value={content}
        onChange={(event) => setContent(event.target.value)}
        className="min-h-20"
      />
      <div className="flex justify-end">
        <Button
          type="button"
          size="sm"
          onClick={handleSubmit}
          disabled={!content.trim()}
          isPending={isPending}
        >
          {parentId ? '답글 작성' : '댓글 작성'}
        </Button>
      </div>
    </div>
  );
}

function EditCommentForm({
  commentId,
  postId,
  initialContent,
  onDone,
}: {
  commentId: number;
  postId: string;
  initialContent: string;
  onDone: () => void;
}) {
  const queryClient = useQueryClient();
  const [content, setContent] = useState(initialContent);
  const { mutate, isPending } = useUpdateComment({
    mutation: {
      onSuccess: () => {
        queryClient.invalidateQueries(
          getGetPostCommentsQueryOptions(postId),
        );
        onDone();
      },
      onError: () => toast.error('댓글을 수정하지 못했습니다.'),
    },
  });

  return (
    <div className="mt-2 space-y-2">
      <Textarea
        value={content}
        onChange={(event) => setContent(event.target.value)}
        className="min-h-20 bg-background"
      />
      <div className="flex justify-end gap-2">
        <Button type="button" variant="ghost" size="sm" onClick={onDone}>
          취소
        </Button>
        <Button
          type="button"
          size="sm"
          disabled={!content.trim()}
          isPending={isPending}
          onClick={() =>
            mutate({ commentId: commentId.toString(), data: { content } })
          }
        >
          저장
        </Button>
      </div>
    </div>
  );
}

function DeleteCommentButton({ commentId }: { commentId: number }) {
  const queryClient = useQueryClient();
  const { mutate, isPending } = useDeleteComment({
    mutation: {
      onSuccess: () =>
        queryClient.invalidateQueries({ queryKey: ['/comments'] }),
      onError: () => toast.error('댓글을 삭제하지 못했습니다.'),
    },
  });

  return (
    <Button
      type="button"
      variant="ghost"
      size="icon-xs"
      isPending={isPending}
      onClick={() => mutate({ commentId: commentId.toString() })}
    >
      <TrashIcon />
    </Button>
  );
}
