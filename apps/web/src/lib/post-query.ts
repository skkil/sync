import { cache } from 'react';

import { getPostBySlug } from '@/api/__generated__/post/post';

/**
 * 메타데이터와 페이지 본문이 같은 요청에서 게시물을 중복 조회하지 않도록 한다.
 * React cache는 요청 단위로만 유지되므로 사용자별 접근 권한이 섞이지 않는다.
 */
export const getPostBySlugCached = cache((slug: string) => getPostBySlug(slug));
