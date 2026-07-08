'use client';

import { BookmarkSimpleIcon } from '@phosphor-icons/react';
import { useIntersectionObserver } from '@uidotdev/usehooks';
import { usePathname, useRouter, useSearchParams } from 'next/navigation';
import { useEffect } from 'react';

import { useGetBookmarkedPostsInfinite } from '@/api/__generated__/bookmark/bookmark';
import { useGetProjectsByUser } from '@/api/__generated__/project/project';
import { PostType } from '@/components/feature/post/types/post';
import PostPreview from '@/components/feature/post/viewer/PostPreview';
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
import { Skeleton } from '@/components/ui/skeleton';
import { Spinner } from '@/components/ui/spinner';
import { useSession } from '@/lib/auth/client';

const BOOKMARKED_POST_PAGE_SIZE = '30';

const ALL_SCOPE = 'all';

export default function BookmarkedPosts() {
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

  const [ref, entry] = useIntersectionObserver({
    threshold: 0.2,
    root: null,
    rootMargin: '400px',
  });

  useEffect(() => {
    if (entry?.isIntersecting && hasNextPage && !isFetchingNextPage) {
      fetchNextPage();
    }
  }, [entry?.isIntersecting, hasNextPage, isFetchingNextPage, fetchNextPage]);

  const posts =
    data?.pages.flatMap((page) => page.data.posts?.nodes ?? []) ?? [];

  if (isPending) {
    return (
      <div className="space-y-4">
        <PageHeader
          scope={scope}
          projects={projects}
          onScopeChange={handleScopeChange}
        />
        {Array.from({ length: 3 }).map((_, index) => (
          <BookmarkedPostSkeleton key={index} />
        ))}
      </div>
    );
  }

  return (
    <div className="space-y-4">
      <PageHeader
        scope={scope}
        projects={projects}
        onScopeChange={handleScopeChange}
      />

      {posts.length === 0 ? (
        <Empty className="min-h-80">
          <EmptyMedia variant="icon">
            <BookmarkSimpleIcon />
          </EmptyMedia>
          <EmptyHeader>
            <EmptyTitle>저장한 포스트가 없습니다</EmptyTitle>
            <EmptyDescription>
              다시 보고 싶은 포스트를 북마크하면 여기에 모입니다.
            </EmptyDescription>
          </EmptyHeader>
        </Empty>
      ) : (
        <div className="space-y-4">
          {posts.map((post) => (
            <PostPreview
              key={post.content.summary.id}
              id={post.content.summary.id}
              slug={post.content.summary.slug}
              type={post.content.summary.type as PostType}
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
      )}

      <div ref={ref} className="py-4">
        {isFetchingNextPage && (
          <div className="flex justify-center">
            <Spinner />
          </div>
        )}
      </div>
    </div>
  );
}

interface PageHeaderProps {
  scope: string;
  projects: { handle: string; name: string }[];
  onScopeChange: (value: string) => void;
}

function PageHeader({ scope, projects, onScopeChange }: PageHeaderProps) {
  return (
    <div className="flex items-start justify-between gap-4">
      <div className="space-y-1">
        <h1 className="text-2xl font-semibold">저장한 포스트</h1>
        <p className="text-sm text-muted-foreground">
          북마크한 포스트를 최근 저장순으로 확인하세요.
        </p>
      </div>

      <Select value={scope} onValueChange={onScopeChange}>
        <SelectTrigger className="w-40">
          <SelectValue />
        </SelectTrigger>
        <SelectContent>
          <SelectItem value={ALL_SCOPE}>전체</SelectItem>
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

function BookmarkedPostSkeleton() {
  return <Skeleton className="h-40 w-full" />;
}
