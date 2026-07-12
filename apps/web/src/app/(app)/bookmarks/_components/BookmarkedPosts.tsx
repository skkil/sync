'use client';

import { BookmarkSimpleIcon } from '@phosphor-icons/react';
import { useTranslations } from 'next-intl';
import { usePathname, useRouter, useSearchParams } from 'next/navigation';

import { useGetBookmarkedPostsInfinite } from '@/api/__generated__/bookmark/bookmark';
import { useGetProjectsByUser } from '@/api/__generated__/project/project';
import PostList from '@/components/feature/post/viewer/PostList';
import { toPostViewSource } from '@/components/feature/post/viewer/types';
import {
  Empty,
  EmptyDescription,
  EmptyHeader,
  EmptyMedia,
  EmptyTitle,
} from '@/components/ui/empty';
import {
  Select,
  SelectContent,
  SelectItem,
  SelectTrigger,
  SelectValue,
} from '@/components/ui/select';
import { useSession } from '@/lib/auth/client';

const BOOKMARKED_POST_PAGE_SIZE = '30';

const ALL_SCOPE = 'all';

export default function BookmarkedPosts() {
  const t = useTranslations('pages.bookmarks');
  const router = useRouter();
  const pathname = usePathname();
  const searchParams = useSearchParams();
  const { data: session } = useSession();

  const scope = searchParams.get('scope') ?? ALL_SCOPE;

  const { data: projectsData } = useGetProjectsByUser(
    session?.user.handle || '',
    { query: { enabled: !!session?.user.handle } },
  );
  const projects = projectsData?.data.projects ?? [];

  const handleScopeChange = (value: string) => {
    const params = new URLSearchParams(searchParams);
    if (value === ALL_SCOPE) {
      params.delete('scope');
    } else {
      params.set('scope', value);
    }
    router.push(`${pathname}?${params.toString()}`);
  };

  const { data, fetchNextPage, hasNextPage, isFetchingNextPage, isPending } =
    useGetBookmarkedPostsInfinite(
      {
        first: BOOKMARKED_POST_PAGE_SIZE,
        after: '',
        projectHandle: scope !== ALL_SCOPE ? scope : undefined,
      },
      {
        query: {
          getNextPageParam: (lastPage) => {
            const posts = lastPage.data.posts;
            return posts?.pageInfo.hasNextPage
              ? posts.pageInfo.endCursor
              : undefined;
          },
        },
      },
    );

  const posts =
    data?.pages.flatMap((page) => page.data.posts?.nodes ?? []) ?? [];

  return (
    <div className="space-y-4">
      <PageHeader
        t={t}
        scope={scope}
        projects={projects}
        onScopeChange={handleScopeChange}
      />

      <PostList
        items={posts.map((post) => toPostViewSource(post.content))}
        isPending={isPending}
        hasNextPage={!!hasNextPage}
        isFetchingNextPage={isFetchingNextPage}
        fetchNextPage={fetchNextPage}
        empty={
          <Empty className="min-h-80">
            <EmptyMedia variant="icon">
              <BookmarkSimpleIcon />
            </EmptyMedia>
            <EmptyHeader>
              <EmptyTitle>{t('empty.title')}</EmptyTitle>
              <EmptyDescription>{t('empty.description')}</EmptyDescription>
            </EmptyHeader>
          </Empty>
        }
      />
    </div>
  );
}

interface PageHeaderProps {
  t: ReturnType<typeof useTranslations<'pages.bookmarks'>>;
  scope: string;
  projects: { handle: string; name: string }[];
  onScopeChange: (value: string) => void;
}

function PageHeader({ t, scope, projects, onScopeChange }: PageHeaderProps) {
  return (
    <div className="flex items-start justify-between gap-4">
      <div className="space-y-1">
        <h1 className="text-2xl font-semibold">{t('title')}</h1>
        <p className="text-sm text-muted-foreground">{t('description')}</p>
      </div>

      <Select value={scope} onValueChange={onScopeChange}>
        <SelectTrigger className="w-40">
          <SelectValue />
        </SelectTrigger>
        <SelectContent>
          <SelectItem value={ALL_SCOPE}>{t('filter.all')}</SelectItem>
          {projects.map((project) => (
            <SelectItem key={project.handle} value={project.handle}>
              {project.name}
            </SelectItem>
          ))}
        </SelectContent>
      </Select>
    </div>
  );
}
