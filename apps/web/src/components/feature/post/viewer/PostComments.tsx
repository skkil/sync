'use client';

import { PaperPlaneRightIcon } from '@phosphor-icons/react';
import { useTranslations } from 'next-intl';
import { useMemo, useState } from 'react';

import { useGetPostCommentsInfinite } from '@/api/__generated__/comment/comment';
import type { GetCommentsResponseCommentsNodesItemContent } from '@/api/__generated__/types';
import { useCreateComment } from '@/components/feature/post/hooks/useCreateComment';
import { ProfileHoverCard } from '@/components/feature/profile/ProfileHoverCard';
import { Avatar, AvatarFallback, AvatarImage } from '@/components/ui/avatar';
import { Badge } from '@/components/ui/badge';
import { Button } from '@/components/ui/button';
import { Card } from '@/components/ui/card';
import { RelativeTime } from '@/components/ui/relative-time';
import { Separator } from '@/components/ui/separator';
import { Skeleton } from '@/components/ui/skeleton';
import { Textarea } from '@/components/ui/textarea';
import { useRequireAuth } from '@/hooks/use-require-auth';
import { useSession } from '@/lib/auth/client';

import type { PostType } from '../types/post';

export const COMMENT_PAGE_SIZE = '20';

interface PostCommentsProps {
  slug: string;
  // TODO: comments aren't rendered differently per post type yet — this is
  // threaded through now so that can change without touching every call site.
  postType: PostType;
}

function PostCommentItem({
  comment,
}: {
  comment: GetCommentsResponseCommentsNodesItemContent;
}) {
  const t = useTranslations('pages.posts.comments');
  const author = comment.author;

  return (
    <div className="flex items-start gap-3 py-4">
      <ProfileHoverCard
        handle={author?.handle ?? ''}
        name={author?.name ?? '?'}
        imageUrl={author?.profileImageUrl ?? undefined}
        size="sm"
      />

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
  const { requireAuth } = useRequireAuth();

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

  const { mutate: createComment, isPending: isSubmitting } = useCreateComment();

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
      { onSuccess: () => setDraft('') },
    );
  }

  return (
    <Card className="gap-0 p-0 lg:max-h-[calc(100vh-7rem)]">
      <div className="flex items-center justify-between px-5 py-4">
        <h2 className="text-sm font-semibold">
          {t('title')} {comments.length > 0 && `· ${comments.length}`}
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

      {!session?.user && (
        <>
          <div className="px-5 py-4">
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
          <PostCommentItem key={comment.id} comment={comment} />
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
