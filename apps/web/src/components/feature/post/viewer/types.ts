import type { GetPostResponseContentMediaItem } from '@/api/__generated__/types';

import { PostScope, PostStatus, PostType } from '../types/post';

export interface PostAuthorSummary {
  name: string;
  handle: string;
  profileImageUrl?: string | null;
}

export interface PostProjectSummary {
  handle?: string | null;
  name?: string | null;
}

export interface PostTagSummary {
  id: number;
  name: string;
  description?: string | null;
  postCount: number;
  followerCount: number;
  projectHandle?: string | null;
  isFollowing: boolean;
}

export interface PostPreviewMedia {
  id: number;
  url: string;
}

export interface PostSummary {
  id: number;
  slug: string;
  type: PostType;
  status: PostStatus;
  scope: PostScope;
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
  tags: PostTagSummary[];
  /** 게시물 내용의 일반 텍스트 미리보기 */
  preview: string;
  /** 게시물 본문의 단어 수 */
  wordCount: number;
  /** 미리보기용 첨부 미디어 목록 (최대 2개) */
  previewMedia: PostPreviewMedia[];
  /** 게시물에 첨부된 전체 미디어 수 */
  mediaCount: number;
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
  'type' | 'status' | 'scope' | 'author' | 'project'
> {
  type: string;
  status: string;
  scope: string;
  author: PostAuthorSummary;
  project?: PostProjectSummary;
}

/**
 * The only place the generated per-endpoint DTOs' string-literal `type`/
 * `status` get narrowed to the app's `PostType`/`PostStatus` enums — every
 * generated summary type is otherwise structurally identical to
 * `PostSummary`.
 */
export function toPostSummary(raw: RawPostSummary): PostSummary {
  return {
    ...raw,
    type: raw.type as PostType,
    status: raw.status as PostStatus,
    scope: raw.scope as PostScope,
  };
}

export function toPostViewSource(raw: {
  summary: RawPostSummary;
  content: PostContent;
}): PostViewSource {
  return {
    summary: toPostSummary(raw.summary),
    content: raw.content,
  };
}
