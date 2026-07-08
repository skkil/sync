'use client';

import { MagnifyingGlassIcon } from '@phosphor-icons/react';
import { useTranslations } from 'next-intl';
import Link from 'next/link';

import { useSearchPosts } from '@/api/__generated__/post/post';
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
        const href = post.summary.project?.handle
          ? ROUTES.PROJECT_POST(post.summary.project.handle, post.summary.slug)
          : ROUTES.POST(post.summary.slug);

        return (
          <Link key={post.summary.id} href={href} className="block py-4">
            <div className="flex items-center gap-2 text-sm">
              <span className="font-semibold">{post.summary.author.name}</span>
              <span className="text-muted-foreground">
                @{post.summary.author.handle} ·{' '}
                <RelativeTime date={post.summary.createdAt} />
              </span>
              {post.summary.project?.name && (
                <Badge variant="secondary">{post.summary.project.name}</Badge>
              )}
            </div>

            {post.summary.title && (
              <h3 className="mt-1 font-semibold">{post.summary.title}</h3>
            )}

            <p className="text-muted-foreground mt-1 line-clamp-2 text-sm">
              {post.content}
            </p>
          </Link>
        );
      })}
    </div>
  );
}
