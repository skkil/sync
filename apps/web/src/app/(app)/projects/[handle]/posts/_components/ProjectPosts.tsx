'use client';

import { useTranslations } from 'next-intl';

import {
  useGetPostsByProjectInfinite,
  useGetPostsByTagInfinite,
} from '@/api/__generated__/post/post';
import { useGetProjectTags } from '@/api/__generated__/tag/tag';
import { PostType } from '@/components/feature/post/types/post';
import PostList from '@/components/feature/post/viewer/PostList';
import PostListMessage from '@/components/feature/post/viewer/error/PostListMessage';
import { toPostViewSource } from '@/components/feature/post/viewer/types';
import { Button, LinkButton } from '@/components/ui/button';
import { useRequireAuth } from '@/hooks/use-require-auth';
import ROUTES from '@/util/routes';

import AddTeammatePopover from './AddTeammatePopover';

const PAGE_SIZE = '10';

interface ProjectPostsProps {
  handle: string;
  type?: string;
  authorHandle?: string;
  tagId?: string;
}

export default function ProjectPosts({
  handle,
  type,
  authorHandle,
  tagId,
}: ProjectPostsProps) {
  const t = useTranslations('pages.projects.project.posts');

  const byProjectQuery = useGetPostsByProjectInfinite(
    handle,
    { first: PAGE_SIZE, type, authorHandle },
    {
      query: {
        enabled: !tagId,
        getNextPageParam: (lastPage) => {
          const pageInfo = lastPage.data.posts?.pageInfo;
          return pageInfo?.hasNextPage
            ? (pageInfo.endCursor ?? undefined)
            : undefined;
        },
      },
    },
  );

  const byTagQuery = useGetPostsByTagInfinite(
    tagId ?? '',
    { first: PAGE_SIZE },
    {
      query: {
        enabled: !!tagId,
        getNextPageParam: (lastPage) => {
          const pageInfo = lastPage.data.posts?.pageInfo;
          return pageInfo?.hasNextPage
            ? (pageInfo.endCursor ?? undefined)
            : undefined;
        },
      },
    },
  );

  const {
    data,
    fetchNextPage,
    hasNextPage,
    isFetchingNextPage,
    isPending,
    isError,
  } = tagId ? byTagQuery : byProjectQuery;

  const posts =
    data?.pages.flatMap((page) => page.data.posts?.nodes ?? []) ?? [];

  const { data: tagsData } = useGetProjectTags(handle, {
    query: { enabled: !!tagId },
  });
  const tagName = tagsData?.data.tags?.find(
    (tag) => String(tag.id) === tagId,
  )?.name;

  const viewTitle = tagId
    ? t('views.tag', { name: tagName ?? '' })
    : authorHandle
      ? t('views.my-posts')
      : type === PostType.QUESTION
        ? t('views.questions')
        : type === PostType.LONG
          ? t('views.guides')
          : t('views.feed');

  return (
    <section className="space-y-3">
      <h1 className="text-xl font-semibold">{viewTitle}</h1>

      <PostList
        items={posts.map((post) => toPostViewSource(post.content))}
        isPending={isPending}
        isError={isError}
        hasNextPage={!!hasNextPage}
        isFetchingNextPage={isFetchingNextPage}
        fetchNextPage={fetchNextPage}
        empty={<ProjectPostsEmpty handle={handle} />}
        error={
          <PostListMessage message={t('list.error')} variant="destructive" />
        }
        end={
          <div className="py-4 text-center">
            <p className="text-xs text-muted-foreground">{t('list.end')}</p>
          </div>
        }
      />
    </section>
  );
}

function ProjectPostsEmpty({ handle }: ProjectPostsProps) {
  const t = useTranslations('pages.projects.project.posts.empty-state');
  const { requireAuth } = useRequireAuth();

  return (
    <div className="flex flex-col items-center gap-6 text-center w-full">
      <div className="space-y-2">
        <p className="font-medium">{t('title')}</p>
        <p className="text-sm text-muted-foreground">{t('description')}</p>
      </div>
      <div className="flex gap-2">
        <AddTeammatePopover
          projectHandle={handle}
          trigger={
            <Button variant="outline" size="sm">
              {t('invite')}
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
          {t('write')}
        </LinkButton>
      </div>
    </div>
  );
}
