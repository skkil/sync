'use client';

import {
  BookmarkSimpleIcon,
  ChatCircleIcon,
  HeartIcon,
} from '@phosphor-icons/react';
import { useState } from 'react';

import { useBookmarkPost } from '@/components/feature/post/hooks/useBookmarkPost';
import { useLikePost } from '@/components/feature/post/hooks/useLikePost';
import { useUnbookmarkPost } from '@/components/feature/post/hooks/useUnbookmarkPost';
import { useUnlikePost } from '@/components/feature/post/hooks/useUnlikePost';
import { Button } from '@/components/ui/button';
import { useRequireAuth } from '@/hooks/use-require-auth';
import { cn } from '@/lib/utils';

function useLikeToggle(
  postId: number,
  initialLiked: boolean,
  initialLikeCount: number,
) {
  const [liked, setLiked] = useState(initialLiked);
  const [likeCount, setLikeCount] = useState(initialLikeCount);

  const { mutate: likePost } = useLikePost();
  const { mutate: unlikePost } = useUnlikePost();

  const toggle = () => {
    const prevLiked = liked;
    const prevLikeCount = likeCount;
    const nextLiked = !liked;
    setLiked(nextLiked);
    setLikeCount((count) => count + (nextLiked ? 1 : -1));

    const mutate = nextLiked ? likePost : unlikePost;
    mutate(
      { postId: String(postId) },
      {
        onError: () => {
          setLiked(prevLiked);
          setLikeCount(prevLikeCount);
        },
      },
    );
  };

  return { liked, likeCount, toggle };
}

function useBookmarkToggle(postId: number, initialBookmarked: boolean) {
  const [bookmarked, setBookmarked] = useState(initialBookmarked);

  const { mutate: bookmarkPost } = useBookmarkPost();
  const { mutate: unbookmarkPost } = useUnbookmarkPost();

  const toggle = () => {
    const prevBookmarked = bookmarked;
    const nextBookmarked = !bookmarked;
    setBookmarked(nextBookmarked);

    const mutate = nextBookmarked ? bookmarkPost : unbookmarkPost;
    mutate(
      { postId: String(postId) },
      {
        onError: () => {
          setBookmarked(prevBookmarked);
        },
      },
    );
  };

  return { bookmarked, toggle };
}

interface PostCardActionsProps {
  postId: number;
  liked: boolean;
  likeCount: number;
  commentCount: number;
  bookmarked: boolean;
  variant?: 'default' | 'bookmark-only';
}

export function PostCardActions({
  postId,
  liked: initialLiked,
  likeCount: initialLikeCount,
  commentCount,
  bookmarked: initialBookmarked,
  variant = 'default',
}: PostCardActionsProps) {
  const { requireAuth } = useRequireAuth();
  const {
    liked,
    likeCount,
    toggle: toggleLike,
  } = useLikeToggle(postId, initialLiked, initialLikeCount);
  const { bookmarked, toggle: toggleBookmark } = useBookmarkToggle(
    postId,
    initialBookmarked,
  );

  const bookmarkButton = (
    <Button
      variant="ghost"
      size="icon-sm"
      onClick={(event) => {
        event.stopPropagation();

        if (!requireAuth({ intent: 'bookmark' })) {
          return;
        }

        toggleBookmark();
      }}
    >
      <BookmarkSimpleIcon
        className={cn(bookmarked && 'fill-primary text-primary')}
        weight={bookmarked ? 'fill' : 'regular'}
      />
    </Button>
  );

  if (variant === 'bookmark-only') {
    return bookmarkButton;
  }

  return (
    <div className="flex items-center justify-between">
      <div className="flex items-center gap-1">
        <Button
          variant="ghost"
          size="sm"
          onClick={(event) => {
            event.stopPropagation();

            if (!requireAuth({ intent: 'like' })) {
              return;
            }

            toggleLike();
          }}
        >
          <HeartIcon
            className={cn(liked && 'fill-destructive text-destructive')}
            weight={liked ? 'fill' : 'regular'}
          />
          {likeCount}
        </Button>

        <Button
          variant="ghost"
          size="sm"
          onClick={(event) => {
            if (!requireAuth({ intent: 'comment' })) {
              event.stopPropagation();
            }
          }}
        >
          <ChatCircleIcon />
          {commentCount}
        </Button>
      </div>

      {bookmarkButton}
    </div>
  );
}
