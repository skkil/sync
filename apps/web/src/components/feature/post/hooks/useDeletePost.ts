import { useQueryClient } from '@tanstack/react-query';

import { useDeletePost as useDeletePostMutation } from '@/api/__generated__/post/post';

function getQueryPath(queryKey: readonly unknown[]) {
  const path = queryKey[0] === 'infinite' ? queryKey[1] : queryKey[0];

  return typeof path === 'string' ? path : '';
}

function isPostRelatedQueryKey(queryKey: readonly unknown[]) {
  const path = getQueryPath(queryKey);

  return (
    path === '/posts' ||
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

export function useDeletePost() {
  const queryClient = useQueryClient();

  return useDeletePostMutation({
    mutation: {
      onSuccess: async () => {
        await queryClient.invalidateQueries({
          predicate: (query) => isPostRelatedQueryKey(query.queryKey),
        });
      },
    },
  });
}
