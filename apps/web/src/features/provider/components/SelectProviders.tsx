import { useDebounce } from '@uidotdev/usehooks';
import { useTranslations } from 'next-intl';
import { useRouter } from 'next/navigation';
import { useState } from 'react';

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

import { useGetProvidersQuery } from '../api/get-providers';

interface SelectProvidersProps {
  placeholder?: string;
  onChange: (value: { type: ProviderType; id: string; name: string }) => void;
  types: ProviderType[];
}

export default function SelectProviders({
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
  } | null>(null);

  const { data: providers, isPending: isGetProvidersPending } =
    useGetProvidersQuery({
      query: debouncedQuery,
      types,
      cursor: '',
      size: 10,
    });

  return (
    <Popover>
      <PopoverTrigger asChild>
        <Button variant="outline">
          {selectedProvider ? selectedProvider.name : placeholder || t('label')}
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
            {isGetProvidersPending ? (
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
                  {providers &&
                    providers.content.map((provider) => (
                      <CommandItem
                        key={provider.id}
                        value={provider.id}
                        onSelect={() => {
                          onChange({
                            type: provider.type,
                            id: provider.id,
                            name: provider.name,
                          });
                          setSelectedProvider({
                            id: provider.id,
                            name: provider.name,
                          });
                        }}
                      >
                        {provider.name}
                      </CommandItem>
                    ))}
                </CommandGroup>
              </>
            )}
          </CommandList>
        </Command>
      </PopoverContent>
    </Popover>
  );
}
