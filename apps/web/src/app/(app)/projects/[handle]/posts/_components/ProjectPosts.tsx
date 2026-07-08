'use client';

import { useIntersectionObserver } from '@uidotdev/usehooks';
import { useTranslations } from 'next-intl';
import { useEffect } from 'react';

import { useGetPostsByProjectInfinite } from '@/api/__generated__/post/post';
import { PostStatus, PostType } from '@/components/feature/post/types/post';
import PostPreview from '@/components/feature/post/viewer/PostPreview';
import { Button, LinkButton } from '@/components/ui/button';
import { Skeleton } from '@/components/ui/skeleton';
import { Spinner } from '@/components/ui/spinner';
import { useRequireAuth } from '@/hooks/use-require-auth';
import ROUTES from '@/util/routes';

import AddTeammatePopover from './AddTeammatePopover';

const PAGE_SIZE = '10';

interface ProjectPostsProps {
  handle: string;
}

export default function ProjectPosts({ handle }: ProjectPostsProps) {
  const t = useTranslations('pages.projects.project.posts');

  const {
    data,
    fetchNextPage,
    hasNextPage,
    isFetchingNextPage,
    isPending,
    isError,
  } = useGetPostsByProjectInfinite(
    handle,
    { first: PAGE_SIZE },
    {
      query: {
        getNextPageParam: (lastPage) => {
          const pageInfo = lastPage.data.posts?.pageInfo;
          return pageInfo?.hasNextPage
            ? (pageInfo.endCursor ?? undefined)
            : undefined;
        },
      },
    },
  );

  const posts =
    data?.pages.flatMap((page) => page.data.posts?.nodes ?? []) ?? [];

  const [ref, entry] = useIntersectionObserver({
    threshold: 0.2,
    root: null,
    rootMargin: '400px',
  });

  useEffect(() => {
    if (entry?.isIntersecting && hasNextPage && !isFetchingNextPage) {
      fetchNextPage();
    }
  }, [entry?.isIntersecting, fetchNextPage, hasNextPage, isFetchingNextPage]);

  if (isPending) {
    return <ProjectPostsSkeleton />;
  }

  if (isError) {
    return (
      <div className="rounded-md border px-4 py-8 text-center">
        <p className="text-sm text-destructive">{t('list.error')}</p>
      </div>
    );
  }

  return (
    <section className="space-y-3">
      {posts.length === 0 ? (
        <ProjectPostsEmpty handle={handle} />
      ) : (
        <>
          <div className="space-y-4">
            {posts.map((post) => (
              <PostPreview
                key={post.content.summary.id}
                id={post.content.summary.id}
                slug={post.content.summary.slug}
                type={post.content.summary.type as PostType}
                status={post.content.summary.status as PostStatus}
                title={post.content.summary.title}
                author={post.content.summary.author}
                project={post.content.summary.project}
                content={{ json: post.content.content, media: [] }}
                liked={post.content.summary.liked}
                likeCount={post.content.summary.likeCount}
                commentCount={post.content.summary.commentCount}
                bookmarked={post.content.summary.bookmarked}
                isAuthor={post.content.summary.isAuthor}
                createdAt={post.content.summary.createdAt}
              />
            ))}
          </div>

          <div ref={ref} className="py-4">
            {isFetchingNextPage && (
              <div className="flex justify-center">
                <Spinner />
              </div>
            )}
          </div>

          {!hasNextPage && (
            <div className="py-4 text-center">
              <p className="text-xs text-muted-foreground">{t('list.end')}</p>
            </div>
          )}
        </>
      )}
    </section>
  );
}

function ProjectPostsSkeleton() {
  return (
    <div className="space-y-4">
      {Array.from({ length: 3 }).map((_, index) => (
        <Skeleton key={index} className="h-32 w-full" />
      ))}
    </div>
  );
}

function ProjectPostsEmpty({ handle }: ProjectPostsProps) {
  const { requireAuth } = useRequireAuth();

  return (
    <div className="flex flex-col items-center gap-6 text-center w-full">
      <div className="space-y-2">
        <p className="font-medium">아직 게시물이 없어요</p>
        <p className="text-sm text-muted-foreground">
          팀원을 초대하거나 첫 번째 게시물을 작성해보세요.
        </p>
      </div>
      <div className="flex gap-2">
        <AddTeammatePopover
          projectHandle={handle}
          trigger={
            <Button variant="outline" size="sm">
              팀원 초대하기
            </Button>
          }
        />
        <LinkButton
          href={ROUTES.NEW_PROJECT_POST(handle)}
          size="sm"
          onClick={(event) => {
            if (
              !requireAuth({
                intent: 'write',
                redirectTo: ROUTES.NEW_PROJECT_POST(handle),
              })
            ) {
              event.preventDefault();
            }
          }}
        >
          첫 게시물 작성하기
        </LinkButton>
      </div>
    </div>
  );
}
