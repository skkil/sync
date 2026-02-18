'use client';

import { MagnifyingGlassIcon } from '@phosphor-icons/react';
import { useTranslations } from 'next-intl';
import { useRouter } from 'next/navigation';
import { useState } from 'react';

import { Input } from '@/components/ui/input';

export default function SearchBar() {
  const t = useTranslations('components.navigation');
  const router = useRouter();

  const [query, setQuery] = useState('');

  return (
    <Input
      startingIcon={<MagnifyingGlassIcon />}
      placeholder={t('search.placeholder')}
      className="w-64"
      value={query}
      onChange={(e) => setQuery(e.target.value)}
      onKeyDown={(e) => {
        if (e.key === 'Enter') {
          e.preventDefault();
          setQuery('');
          router.push(`/search?q=${encodeURIComponent(query)}`);
        }
      }}
    />
  );
}
