import { useDebounce, useIntersectionObserver } from '@uidotdev/usehooks';
import { useTranslations } from 'next-intl';
import { useRouter } from 'next/navigation';
import { useEffect, useState } from 'react';

import { useSearchProvidersInfinite } from '@/api/__generated__/providers/providers';
import { Button } from '@/components/ui/button';
import {
  Command,
  CommandEmpty,
  CommandGroup,
  CommandInput,
  CommandItem,
  CommandList,
} from '@/components/ui/command';
import {
  Popover,
  PopoverContent,
  PopoverTrigger,
} from '@/components/ui/popover';
import { Spinner } from '@/components/ui/spinner';
import { ProviderType } from '@/types/provider';

const DEFAULT_PROVIDER_PAGE_SIZE = 10;

interface SelectProvidersProps {
  defaultValue?: { type: ProviderType; id: string; name: string };
  placeholder?: string;
  onChange: (value: { type: ProviderType; id: string; name: string }) => void;
  types: ProviderType[];
}

export default function SelectProviders({
  defaultValue,
  placeholder,
  onChange,
  types,
}: SelectProvidersProps) {
  const t = useTranslations('components.select-providers');
  const router = useRouter();

  const [query, setQuery] = useState('');
  const debouncedQuery = useDebounce(query, 300);

  const [selectedProvider, setSelectedProvider] = useState<{
    id: string;
    name: string;
  } | null>(
    defaultValue ? { id: defaultValue.id, name: defaultValue.name } : null,
  );

  const { data, fetchNextPage, hasNextPage, isFetchingNextPage } =
    useSearchProvidersInfinite(
      {
        query: debouncedQuery,
        types: types.join(','),
        first: DEFAULT_PROVIDER_PAGE_SIZE.toString(),
      },
      {
        query: {
          enabled: debouncedQuery.length > 0,
          getNextPageParam: (lastPage) => {
            const providers = lastPage.data.providers;
            return providers?.pageInfo.hasNextPage
              ? providers.pageInfo.endCursor
              : undefined;
          },
        },
      },
    );

  const [ref, entry] = useIntersectionObserver({
    threshold: 0.2,
    root: null,
    rootMargin: '100px',
  });

  useEffect(() => {
    if (entry?.isIntersecting && hasNextPage && !isFetchingNextPage) {
      fetchNextPage();
    }
  }, [entry?.isIntersecting, hasNextPage, isFetchingNextPage, fetchNextPage]);

  const providers =
    data?.pages.flatMap((page) => page.data.providers?.nodes ?? []) ?? [];

  return (
    <Popover>
      <PopoverTrigger asChild>
        <Button variant="outline">
          {selectedProvider && selectedProvider.name !== ''
            ? selectedProvider.name
            : placeholder || t('label')}
        </Button>
      </PopoverTrigger>

      <PopoverContent
        aria-modal={true}
        className="z-50 pointer-events-auto p-0 w-[var(--radix-popover-trigger-width)]"
      >
        <Command shouldFilter={false}>
          <CommandInput
            value={query}
            onValueChange={(content) => setQuery(content)}
          />
          <CommandList>
            {isFetchingNextPage ? (
              <div className="flex items-center justify-center py-4">
                <Spinner />
              </div>
            ) : (
              <>
                <CommandEmpty>
                  {query.length > 0 ? (
                    <>
                      <div>{t('provider-not-found')}</div>

                      <div
                        className="text-xs hover:underline cursor-pointer"
                        onClick={(e) => {
                          e.stopPropagation();
                          router.push('/provider/create');
                        }}
                      >
                        {t('create-link')}
                      </div>
                    </>
                  ) : (
                    <div>{t('enter-query')}</div>
                  )}
                </CommandEmpty>
                <CommandGroup>
                  {providers.map(({ content: { id, type, name } }) => (
                    <CommandItem
                      key={id}
                      value={id}
                      onSelect={() => {
                        onChange({
                          type: type as ProviderType,
                          id,
                          name,
                        });
                        setSelectedProvider({
                          id,
                          name,
                        });
                      }}
                    >
                      {name}
                    </CommandItem>
                  ))}
                </CommandGroup>
                {hasNextPage && (
                  <div ref={ref} className="py-2">
                    {isFetchingNextPage && (
                      <div className="flex items-center justify-center">
                        <Spinner />
                      </div>
                    )}
                  </div>
                )}
              </>
            )}
          </CommandList>
        </Command>
      </PopoverContent>
    </Popover>
  );
}
