'use client';

import { BookmarkSimpleIcon } from '@phosphor-icons/react';
import { type InfiniteData, useQueryClient } from '@tanstack/react-query';
import { toast } from 'sonner';

import {
  getGetBookmarkedReflectionsInfiniteQueryKey,
  getGetBookmarkedReflectionsQueryKey,
  useBookmarkReflection,
  useUnbookmarkReflection,
} from '@/api/__generated__/bookmark/bookmark';
import {
  type GetRecentFeedInfiniteQueryResult,
  getGetRecentFeedInfiniteQueryKey,
} from '@/api/__generated__/feed/feed';
import {
  type GetReflectionBySlugQueryResult,
  getGetReflectionBySlugQueryKey,
} from '@/api/__generated__/reflection/reflection';
import { Button } from '@/components/ui/button';
import { cn } from '@/lib/utils';

type RecentFeedInfiniteData = InfiniteData<
  GetRecentFeedInfiniteQueryResult,
  unknown
>;

interface ReflectionBookmarkButtonProps {
  reflectionId: number;
  bookmarked: boolean;
  slug?: string;
  isAuthenticated?: boolean;
  className?: string;
  size?: 'sm' | 'icon-sm';
}

export default function ReflectionBookmarkButton({
  reflectionId,
  bookmarked,
  slug,
  isAuthenticated,
  className,
  size = 'icon-sm',
}: ReflectionBookmarkButtonProps) {
  const queryClient = useQueryClient();
  const bookmarkMutation = useBookmarkReflection();
  const unbookmarkMutation = useUnbookmarkReflection();
  const isPending = bookmarkMutation.isPending || unbookmarkMutation.isPending;

  const syncBookmarkState = (nextBookmarked: boolean) => {
    queryClient.setQueriesData<RecentFeedInfiniteData>(
      { queryKey: getGetRecentFeedInfiniteQueryKey() },
      (data) => updateRecentFeedBookmark(data, reflectionId, nextBookmarked),
    );

    if (slug) {
      queryClient.setQueryData<GetReflectionBySlugQueryResult>(
        getGetReflectionBySlugQueryKey(slug),
        (data) =>
          updateReflectionDetailBookmark(data, reflectionId, nextBookmarked),
      );
    }
  };

  const invalidateBookmarkState = () => {
    void queryClient.invalidateQueries({
      queryKey: getGetRecentFeedInfiniteQueryKey(),
    });
    void queryClient.invalidateQueries({
      queryKey: getGetBookmarkedReflectionsInfiniteQueryKey(),
    });
    void queryClient.invalidateQueries({
      queryKey: getGetBookmarkedReflectionsQueryKey(),
    });

    if (slug) {
      void queryClient.invalidateQueries({
        queryKey: getGetReflectionBySlugQueryKey(slug),
      });
    }
  };

  const handleClick = () => {
    if (isAuthenticated === false) {
      toast.error('로그인이 필요합니다.');
      return;
    }

    const nextBookmarked = !bookmarked;
    const mutation = nextBookmarked ? bookmarkMutation : unbookmarkMutation;

    syncBookmarkState(nextBookmarked);
    mutation.mutate(
      { reflectionId: String(reflectionId) },
      {
        onError: () => {
          syncBookmarkState(bookmarked);
          toast.error(
            nextBookmarked
              ? '북마크를 저장하지 못했습니다.'
              : '북마크를 해제하지 못했습니다.',
          );
        },
        onSuccess: invalidateBookmarkState,
      },
    );
  };

  return (
    <Button
      type="button"
      variant="ghost"
      size={size}
      disabled={isPending}
      aria-pressed={bookmarked}
      aria-label={bookmarked ? '북마크 해제' : '북마크 저장'}
      title={bookmarked ? '북마크 해제' : '북마크 저장'}
      className={cn(bookmarked && 'text-primary', className)}
      onClick={handleClick}
    >
      <BookmarkSimpleIcon weight={bookmarked ? 'fill' : 'regular'} />
    </Button>
  );
}

function updateRecentFeedBookmark(
  data: RecentFeedInfiniteData | undefined,
  reflectionId: number,
  bookmarked: boolean,
) {
  if (!data) {
    return data;
  }

  return {
    ...data,
    pages: data.pages.map((page) => {
      const items = page.data.items;

      if (!items) {
        return page;
      }

      return {
        ...page,
        data: {
          ...page.data,
          items: {
            ...items,
            nodes: items.nodes.map((node) =>
              node.content.id === reflectionId
                ? {
                    ...node,
                    content: {
                      ...node.content,
                      bookmarked,
                    },
                  }
                : node,
            ),
          },
        },
      };
    }),
  };
}

function updateReflectionDetailBookmark(
  data: GetReflectionBySlugQueryResult | undefined,
  reflectionId: number,
  bookmarked: boolean,
) {
  if (!data || data.data.id !== reflectionId) {
    return data;
  }

  return {
    ...data,
    data: {
      ...data.data,
      bookmarked,
    },
  };
}
