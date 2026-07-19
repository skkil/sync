'use client';

import { useTranslations } from 'next-intl';

import { useGetCommentedPostsInfinite } from '@/api/__generated__/post/post';
import PostList from '@/components/feature/post/viewer/PostList';
import PostListMessage from '@/components/feature/post/viewer/error/PostListMessage';
import { toPostSummary } from '@/components/feature/post/viewer/types';
import { useSession } from '@/lib/auth/client';

const PAGE_SIZE = '10';

interface ProjectMyCommentsProps {
  handle: string;
}

export default function ProjectMyComments({ handle }: ProjectMyCommentsProps) {
  const t = useTranslations('pages.projects.project.my-comments');
  const { data: session } = useSession();
  const userId = session?.user.id;

  const {
    data,
    fetchNextPage,
    hasNextPage,
    isFetchingNextPage,
    isPending,
    isError,
  } = useGetCommentedPostsInfinite(
    userId ?? '',
    { first: PAGE_SIZE, projectHandle: handle },
    {
      query: {
        enabled: !!userId,
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

  return (
    <section className="space-y-3">
      <h1 className="text-xl font-semibold">{t('heading')}</h1>

      <PostList
        items={posts.map((post) => toPostSummary(post.content))}
        isPending={isPending || !userId}
        isError={isError}
        hasNextPage={!!hasNextPage}
        isFetchingNextPage={isFetchingNextPage}
        fetchNextPage={fetchNextPage}
        empty={<PostListMessage message={t('empty')} />}
        error={<PostListMessage message={t('error')} variant="destructive" />}
        end={
          <div className="py-4 text-center">
            <p className="text-muted-foreground text-xs">{t('end')}</p>
          </div>
        }
      />
    </section>
  );
}
