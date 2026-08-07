import type { QueryClient } from '@tanstack/react-query';

export const BOOKMARKED_POSTS_PATH = '/bookmarks/posts';
export const LIKED_POSTS_PATH = '/posts/likes';

export function pinnedPostsPath(projectHandle: string) {
  return `/projects/${projectHandle}/posts/pinned`;
}

export function getQueryPath(queryKey: readonly unknown[]) {
  const path = queryKey[0] === 'infinite' ? queryKey[1] : queryKey[0];

  return typeof path === 'string' ? path : '';
}

export function isPostRelatedQueryKey(queryKey: readonly unknown[]) {
  const path = getQueryPath(queryKey);

  return (
    path === '/posts' ||
    path === '/posts/drafts' ||
    path === '/posts/likes' ||
    path === '/posts/recommendations' ||
    path.startsWith('/posts/') ||
    path === '/bookmarks/posts' ||
    path === '/search/posts' ||
    /^\/users\/[^/]+\/posts$/.test(path) ||
    /^\/projects\/[^/]+\/posts(\/pinned)?$/.test(path) ||
    /^\/profiles\/[^/]+\/posts\/activities$/.test(path) ||
    /^\/tags\/[^/]+\/posts$/.test(path) ||
    /^\/collections\/[^/]+\/posts$/.test(path)
  );
}

export function isPostRecommendationQueryKey(queryKey: readonly unknown[]) {
  return getQueryPath(queryKey) === '/posts/recommendations';
}

export function invalidatePostRecommendationQueries(queryClient: QueryClient) {
  return queryClient.invalidateQueries({
    predicate: (query) => isPostRecommendationQueryKey(query.queryKey),
  });
}

export function invalidatePostQueries(queryClient: QueryClient) {
  return queryClient.invalidateQueries({
    predicate: (query) => isPostRelatedQueryKey(query.queryKey),
  });
}
