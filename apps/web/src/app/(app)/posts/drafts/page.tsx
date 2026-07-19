'use client';

import { NotePencilIcon } from '@phosphor-icons/react';
import { useTranslations } from 'next-intl';
import { useMemo, useState } from 'react';

import { useGetDraftPostsInfinite } from '@/api/__generated__/post/post';
import { PostScope, PostType } from '@/components/feature/post/types/post';
import PostList from '@/components/feature/post/viewer/PostList';
import PostListMessage from '@/components/feature/post/viewer/error/PostListMessage';
import { toPostSummary } from '@/components/feature/post/viewer/types';
import { LinkButton } from '@/components/ui/button';
import {
  Select,
  SelectContent,
  SelectItem,
  SelectTrigger,
  SelectValue,
} from '@/components/ui/select';
import { Tabs, TabsList, TabsTrigger } from '@/components/ui/tabs';
import { useAuthGuard } from '@/hooks/use-auth-guard';
import ROUTES from '@/util/routes';

type ScopeFilter = 'ALL' | PostScope;
type TypeFilter = 'ALL' | PostType;

const PAGE_SIZE = '50';

export default function DraftPostsPage() {
  useAuthGuard();
  const t = useTranslations('pages.posts.drafts');
  const tPost = useTranslations('components.post');

  const [scope, setScope] = useState<ScopeFilter>('ALL');
  const [type, setType] = useState<TypeFilter>('ALL');

  const params = useMemo(
    () => ({
      first: PAGE_SIZE,
      scope: scope === 'ALL' ? undefined : scope,
      type: type === 'ALL' ? undefined : type,
    }),
    [scope, type],
  );

  const {
    data,
    fetchNextPage,
    hasNextPage,
    isError,
    isFetchingNextPage,
    isPending,
  } = useGetDraftPostsInfinite(params, {
    query: {
      getNextPageParam: (lastPage) => {
        const pageInfo = lastPage.data.posts?.pageInfo;
        return pageInfo?.hasNextPage
          ? (pageInfo.endCursor ?? undefined)
          : undefined;
      },
    },
  });
  const drafts =
    data?.pages.flatMap((page) => page.data.posts?.nodes ?? []) ?? [];

  return (
    <main className="mx-auto flex w-full max-w-4xl flex-col gap-5 px-4 py-8">
      <div className="flex flex-wrap items-center justify-between gap-3">
        <div className="flex items-center gap-2">
          <NotePencilIcon className="size-5 text-muted-foreground" />
          <h1 className="text-xl font-semibold">{t('title')}</h1>
        </div>

        <LinkButton href={ROUTES.NEW_POST()} variant="outline">
          {t('new-post')}
        </LinkButton>
      </div>

      <div className="flex flex-wrap items-center gap-3">
        <Tabs
          value={scope}
          onValueChange={(value) => setScope(value as ScopeFilter)}
        >
          <TabsList>
            <TabsTrigger value="ALL">{t('filters.all')}</TabsTrigger>
            <TabsTrigger value={PostScope.PUBLIC}>
              {t('filters.public')}
            </TabsTrigger>
            <TabsTrigger value={PostScope.WORKSPACE}>
              {t('filters.workspace')}
            </TabsTrigger>
          </TabsList>
        </Tabs>

        <Select
          value={type}
          onValueChange={(value) => setType(value as TypeFilter)}
        >
          <SelectTrigger className="w-40">
            <SelectValue />
          </SelectTrigger>
          <SelectContent>
            <SelectItem value="ALL">{t('filters.all-types')}</SelectItem>
            <SelectItem value={PostType.SHORT}>
              {tPost('type.SHORT')}
            </SelectItem>
            <SelectItem value={PostType.LONG}>{tPost('type.LONG')}</SelectItem>
            <SelectItem value={PostType.QUESTION}>
              {tPost('type.QUESTION')}
            </SelectItem>
          </SelectContent>
        </Select>
      </div>

      <PostList
        items={drafts.map((draft) => toPostSummary(draft.content))}
        isPending={isPending}
        isError={isError}
        hasNextPage={!!hasNextPage}
        isFetchingNextPage={isFetchingNextPage}
        fetchNextPage={fetchNextPage}
        empty={
          <div className="flex flex-col items-center gap-3 py-16 text-center">
            <p className="font-medium">{t('empty.title')}</p>
            <p className="text-sm text-muted-foreground">
              {t('empty.description')}
            </p>
            <LinkButton href={ROUTES.NEW_POST()}>
              {t('empty.action')}
            </LinkButton>
          </div>
        }
        error={
          <PostListMessage
            message={t('error.description')}
            variant="destructive"
          />
        }
      />
    </main>
  );
}
