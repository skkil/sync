'use client';

import { MagnifyingGlassIcon } from '@phosphor-icons/react';
import { useTranslations } from 'next-intl';
import Link from 'next/link';

import { useSearchPosts } from '@/api/__generated__/post/post';
import { PostTagChips } from '@/components/feature/post/viewer/components/PostTagChips';
import { Badge } from '@/components/ui/badge';
import {
  Empty,
  EmptyHeader,
  EmptyMedia,
  EmptyTitle,
} from '@/components/ui/empty';
import { RelativeTime } from '@/components/ui/relative-time';
import { Skeleton } from '@/components/ui/skeleton';
import ROUTES from '@/util/routes';

interface SearchPostResultsProps {
  query: string;
  projectHandle?: string;
}

export default function SearchPostResults({
  query,
  projectHandle,
}: SearchPostResultsProps) {
  const t = useTranslations('pages.search');
  const { data, isPending } = useSearchPosts(
    { query, projectHandle },
    { query: { enabled: !!query } },
  );

  if (isPending) {
    return (
      <div className="space-y-4">
        {Array.from({ length: 3 }).map((_, index) => (
          <Skeleton key={index} className="h-20 w-full" />
        ))}
      </div>
    );
  }

  const posts = data?.data.posts ?? [];

  if (posts.length === 0) {
    return (
      <Empty className="min-h-60">
        <EmptyMedia variant="icon">
          <MagnifyingGlassIcon />
        </EmptyMedia>
        <EmptyHeader>
          <EmptyTitle>{t('results.no-results', { query })}</EmptyTitle>
        </EmptyHeader>
      </Empty>
    );
  }

  return (
    <div className="divide-y">
      {posts.map((post) => {
        const href = post.project?.handle
          ? ROUTES.PROJECT_POST(post.project.handle, post.slug)
          : ROUTES.POST(post.slug);

        return (
          <article key={post.id} className="py-4">
            <Link href={href} className="block">
              <div className="flex items-center gap-2 text-sm">
                <span className="font-semibold">{post.author.name}</span>
                <span className="text-muted-foreground">
                  @{post.author.handle} · <RelativeTime date={post.createdAt} />
                </span>
                {post.project?.name && (
                  <Badge variant="secondary">{post.project.name}</Badge>
                )}
              </div>

              {post.title && (
                <h3 className="mt-1 font-semibold">{post.title}</h3>
              )}

              <p className="text-muted-foreground mt-1 line-clamp-2 text-sm">
                {post.preview}
              </p>
            </Link>

            {post.tags.length > 0 && (
              <div className="mt-2">
                <PostTagChips tags={post.tags} />
              </div>
            )}
          </article>
        );
      })}
    </div>
  );
}
