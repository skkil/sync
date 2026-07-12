import type { GetPostResponseContentMediaItem } from '@/api/__generated__/types';

import type { PostContent } from '../types';

export function normalizePostContent(content: PostContent): {
  json: string;
  media: GetPostResponseContentMediaItem[];
} {
  return typeof content === 'string' ? { json: content, media: [] } : content;
}
