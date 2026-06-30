'use client';

import {
  BookmarkSimpleIcon,
  ChatCircleIcon,
  HeartIcon,
} from '@phosphor-icons/react';
import { useState } from 'react';

import {
  useBookmarkPost,
  useUnbookmarkPost,
} from '@/api/__generated__/bookmark/bookmark';
import { useLikePost, useUnlikePost } from '@/api/__generated__/post/post';
import { Button } from '@/components/ui/button';
import { cn } from '@/lib/utils';

function useLikeToggle(postId: number, initialLikeCount: number) {
  const [liked, setLiked] = useState(false);
  const [likeCount, setLikeCount] = useState(initialLikeCount);

  const { mutate: likePost } = useLikePost();
  const { mutate: unlikePost } = useUnlikePost();

  const toggle = () => {
    const nextLiked = !liked;
    setLiked(nextLiked);
    setLikeCount((count) => count + (nextLiked ? 1 : -1));

    const mutate = nextLiked ? likePost : unlikePost;
    mutate({ postId: String(postId) });
  };

  return { liked, likeCount, toggle };
}

function useBookmarkToggle(postId: number, initialBookmarked: boolean) {
  const [bookmarked, setBookmarked] = useState(initialBookmarked);

  const { mutate: bookmarkPost } = useBookmarkPost();
  const { mutate: unbookmarkPost } = useUnbookmarkPost();

  const toggle = () => {
    const nextBookmarked = !bookmarked;
    setBookmarked(nextBookmarked);

    const mutate = nextBookmarked ? bookmarkPost : unbookmarkPost;
    mutate({ postId: String(postId) });
  };

  return { bookmarked, toggle };
}

interface PostCardActionsProps {
  postId: number;
  likeCount: number;
  commentCount: number;
  bookmarked: boolean;
}

export function PostCardActions({
  postId,
  likeCount: initialLikeCount,
  commentCount,
  bookmarked: initialBookmarked,
}: PostCardActionsProps) {
  const {
    liked,
    likeCount,
    toggle: toggleLike,
  } = useLikeToggle(postId, initialLikeCount);
  const { bookmarked, toggle: toggleBookmark } = useBookmarkToggle(
    postId,
    initialBookmarked,
  );

  return (
    <div className="flex items-center justify-between">
      <div className="flex items-center gap-1">
        <Button variant="ghost" size="sm" onClick={toggleLike}>
          <HeartIcon
            className={cn(liked && 'fill-destructive text-destructive')}
            weight={liked ? 'fill' : 'regular'}
          />
          {likeCount}
        </Button>

        <Button variant="ghost" size="sm">
          <ChatCircleIcon />
          {commentCount}
        </Button>
      </div>

      <Button variant="ghost" size="icon-sm" onClick={toggleBookmark}>
        <BookmarkSimpleIcon
          className={cn(bookmarked && 'fill-primary text-primary')}
          weight={bookmarked ? 'fill' : 'regular'}
        />
      </Button>
    </div>
  );
}
