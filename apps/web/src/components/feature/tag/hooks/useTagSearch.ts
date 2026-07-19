import { useDebounce } from '@uidotdev/usehooks';
import { useState } from 'react';

import { useSearchTags } from '@/api/__generated__/tag/tag';

const DEBOUNCE_MS = 300;

interface UseTagSearchOptions {
  handle?: string;
}

export function useTagSearch(options: UseTagSearchOptions = {}) {
  const [query, setQuery] = useState('');
  const debouncedQuery = useDebounce(query, DEBOUNCE_MS);

  const { data, isFetching } = useSearchTags(
    { query: debouncedQuery, handle: options.handle },
    { query: { enabled: debouncedQuery.trim().length > 0 } },
  );

  const isPending = query !== debouncedQuery || isFetching;

  return {
    query,
    setQuery,
    debouncedQuery,
    tags: data?.data.tags ?? [],
    isPending,
  };
}
