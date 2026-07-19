import type { QueryClient } from '@tanstack/react-query';

function getQueryPath(queryKey: readonly unknown[]) {
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
    /^\/projects\/[^/]+\/posts$/.test(path) ||
    /^\/profiles\/[^/]+\/posts\/activities$/.test(path)
  );
}

export function invalidatePostQueries(queryClient: QueryClient) {
  return queryClient.invalidateQueries({
    predicate: (query) => isPostRelatedQueryKey(query.queryKey),
  });
}
