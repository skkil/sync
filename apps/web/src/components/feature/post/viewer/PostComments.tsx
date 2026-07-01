'use client';

import { PaperPlaneRightIcon } from '@phosphor-icons/react';
import { useTranslations } from 'next-intl';
import { useMemo, useState } from 'react';

import {
  getGetPostCommentsQueryKey,
  useCreateComment,
  useGetPostComments,
} from '@/api/__generated__/comment/comment';
import type { GetCommentsResponseCommentsItem } from '@/api/__generated__/types';
import { Avatar, AvatarFallback, AvatarImage } from '@/components/ui/avatar';
import { Badge } from '@/components/ui/badge';
import { Button } from '@/components/ui/button';
import { Card } from '@/components/ui/card';
import { RelativeTime } from '@/components/ui/relative-time';
import { Separator } from '@/components/ui/separator';
import { Skeleton } from '@/components/ui/skeleton';
import { Textarea } from '@/components/ui/textarea';
import { useSession } from '@/lib/auth/client';

interface PostCommentsProps {
  slug: string;
}

function PostCommentItem({
  comment,
}: {
  comment: GetCommentsResponseCommentsItem;
}) {
  const t = useTranslations('pages.posts.comments');
  const author = comment.author;

  return (
    <div className="flex items-start gap-3 py-4">
      <Avatar size="sm">
        <AvatarImage
          src={author?.profileImageUrl ?? undefined}
          alt={author?.name}
        />
        <AvatarFallback>{author?.name?.[0] ?? '?'}</AvatarFallback>
      </Avatar>

      <div className="flex min-w-0 flex-1 flex-col gap-1">
        <div className="flex flex-wrap items-center gap-2">
          <span className="text-sm font-semibold">{author?.name}</span>
          {author?.isPostAuthor && (
            <Badge variant="secondary" className="text-[10px]">
              {t('author-badge')}
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
      </div>
    </div>
  );
}

export default function PostComments({ slug }: PostCommentsProps) {
  const t = useTranslations('pages.posts.comments');
  const { data: session } = useSession();

  const { data, isPending } = useGetPostComments(slug);
  const [draft, setDraft] = useState('');

  const { mutate: createComment, isPending: isSubmitting } = useCreateComment({
    mutation: {
      onSuccess: async (_data, _variables, _onMutateResult, context) => {
        setDraft('');
        await context.client.invalidateQueries({
          queryKey: getGetPostCommentsQueryKey(slug),
        });
      },
    },
  });

  const comments = useMemo(() => {
    return [...(data?.data.comments ?? [])].sort(
      (a, b) =>
        new Date(a.createdAt).getTime() - new Date(b.createdAt).getTime(),
    );
  }, [data]);

  function handleSubmit() {
    const content = draft.trim();
    if (!content) {
      return;
    }

    createComment({ slug, data: { content } });
  }

  return (
    <Card className="gap-0 p-0">
      <div className="flex items-center justify-between px-5 py-4">
        <h2 className="text-sm font-semibold">
          {t('title')} {data && `· ${comments.length}`}
        </h2>
      </div>

      <Separator />

      {session?.user && (
        <>
          <div className="flex items-start gap-3 px-5 py-4">
            <Avatar size="sm">
              <AvatarImage
                src={session.user.image ?? undefined}
                alt={session.user.name}
              />
              <AvatarFallback>{session.user.name?.[0]}</AvatarFallback>
            </Avatar>

            <div className="flex flex-1 flex-col gap-2">
              <Textarea
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

      <div className="divide-border/60 divide-y px-5">
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
          <PostCommentItem key={comment.id} comment={comment} />
        ))}
      </div>
    </Card>
  );
}
