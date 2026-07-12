import type { GetPostResponseContentMediaItem } from '@/api/__generated__/types';

import { PostStatus, PostType } from '../types/post';

export interface PostAuthorSummary {
  name: string;
  handle: string;
  profileImageUrl?: string | null;
}

export interface PostProjectSummary {
  handle?: string | null;
  name?: string | null;
}

export interface PostSummary {
  id: number;
  slug: string;
  type: PostType;
  status: PostStatus;
  title?: string | null;
  author: PostAuthorSummary;
  project?: PostProjectSummary;
  liked: boolean;
  likeCount: number;
  bookmarked: boolean;
  commentCount: number;
  isAuthor: boolean;
  createdAt: string;
  resolved: boolean;
}

export type PostContent =
  | string
  | { json: string; media: GetPostResponseContentMediaItem[] };

export interface PostViewSource {
  summary: PostSummary;
  content: PostContent;
}

export type PostCardVariant = 'preview' | 'detail';

interface RawPostSummary extends Omit<
  PostSummary,
  'type' | 'status' | 'author' | 'project'
> {
  type: string;
  status: string;
  author: PostAuthorSummary;
  project?: PostProjectSummary;
}

/**
 * The only place the generated per-endpoint DTOs' string-literal `type`/
 * `status` get narrowed to the app's `PostType`/`PostStatus` enums — every
 * generated summary type is otherwise structurally identical to
 * `PostSummary`.
 */
export function toPostViewSource(raw: {
  summary: RawPostSummary;
  content: PostContent;
}): PostViewSource {
  return {
    summary: {
      ...raw.summary,
      type: raw.summary.type as PostType,
      status: raw.summary.status as PostStatus,
    },
    content: raw.content,
  };
}
