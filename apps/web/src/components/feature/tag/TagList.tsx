import Link from 'next/link';
import { Key, ReactNode } from 'react';

import { TagBadge } from '@/components/feature/tag/TagBadge';
import {
  Pagination,
  PaginationContent,
  PaginationItem,
  PaginationNext,
  PaginationPrevious,
} from '@/components/ui/pagination';
import { Skeleton } from '@/components/ui/skeleton';
import { cn } from '@/lib/utils';

interface TagListItemProps {
  name: string;
  description: string;
  noDescriptionLabel: string;
  postCountLabel: string;
  isProjectTag?: boolean;
  href?: string;
  trailing?: ReactNode;
  className?: string;
}

export function TagListItem({
  name,
  description,
  noDescriptionLabel,
  postCountLabel,
  isProjectTag,
  href,
  trailing,
  className,
}: TagListItemProps) {
  const content = (
    <>
      <TagBadge
        name={name}
        isProjectTag={isProjectTag}
        variant="secondary"
        className="w-fit shrink-0"
      />

      <p className="flex-1 truncate text-sm text-muted-foreground">
        {description || noDescriptionLabel}
      </p>

      <p className="shrink-0 text-xs text-muted-foreground">{postCountLabel}</p>

      {trailing}
    </>
  );

  const rowClassName = cn(
    'flex items-center gap-4 rounded-md border p-4',
    href && 'hover:bg-accent',
    className,
  );

  if (href) {
    return (
      <Link href={href} className={rowClassName}>
        {content}
      </Link>
    );
  }

  return <div className={rowClassName}>{content}</div>;
}

interface TagListMessageProps {
  message: string;
  variant?: 'empty' | 'error';
}

export function TagListMessage({
  message,
  variant = 'empty',
}: TagListMessageProps) {
  return (
    <div className="rounded-md border px-4 py-8 text-center">
      <p
        className={cn(
          'text-sm',
          variant === 'error' ? 'text-destructive' : 'text-muted-foreground',
        )}
      >
        {message}
      </p>
    </div>
  );
}

interface TagListSkeletonProps {
  count?: number;
  className?: string;
}

export function TagListSkeleton({
  count = 6,
  className,
}: TagListSkeletonProps) {
  return (
    <div className={cn('flex flex-col gap-3', className)}>
      {Array.from({ length: count }).map((_, index) => (
        <Skeleton key={index} className="h-16 w-full rounded-md" />
      ))}
    </div>
  );
}

interface TagListPageInfo {
  hasPreviousPage: boolean;
  hasNextPage: boolean;
}

interface TagListProps<T> {
  tags: T[];
  getKey: (tag: T) => Key;
  renderItem: (tag: T) => ReactNode;
  isPending?: boolean;
  isError?: boolean;
  emptyMessage: string;
  errorMessage: string;
  pageInfo?: TagListPageInfo;
  onPreviousPage?: () => void;
  onNextPage?: () => void;
  className?: string;
}

export function TagList<T>({
  tags,
  getKey,
  renderItem,
  isPending,
  isError,
  emptyMessage,
  errorMessage,
  pageInfo,
  onPreviousPage,
  onNextPage,
  className,
}: TagListProps<T>) {
  return (
    <div className={cn('space-y-4', className)}>
      {isPending ? (
        <TagListSkeleton />
      ) : isError ? (
        <TagListMessage variant="error" message={errorMessage} />
      ) : tags.length === 0 ? (
        <TagListMessage message={emptyMessage} />
      ) : (
        <div className="flex flex-col gap-3">
          {tags.map((tag) => (
            <div key={getKey(tag)}>{renderItem(tag)}</div>
          ))}
        </div>
      )}

      {pageInfo && (pageInfo.hasPreviousPage || pageInfo.hasNextPage) && (
        <Pagination>
          <PaginationContent>
            <PaginationItem>
              <PaginationPrevious
                onClick={onPreviousPage}
                disabled={!pageInfo.hasPreviousPage}
              />
            </PaginationItem>
            <PaginationItem>
              <PaginationNext
                onClick={onNextPage}
                disabled={!pageInfo.hasNextPage}
              />
            </PaginationItem>
          </PaginationContent>
        </Pagination>
      )}
    </div>
  );
}
