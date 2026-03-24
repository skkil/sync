'use client';

import { MagnifyingGlassIcon, XIcon } from '@phosphor-icons/react';
import { useTranslations } from 'next-intl';
import { useRouter } from 'next/navigation';
import { useRef, useState } from 'react';

import { Button } from '@/components/ui/button';
import {
  InputGroup,
  InputGroupAddon,
  InputGroupInput,
} from '@/components/ui/input-group';

export default function SearchBar() {
  const t = useTranslations('components.navigation');
  const router = useRouter();
  const inputRef = useRef<HTMLInputElement>(null);

  const [query, setQuery] = useState('');
  const [isExpanded, setIsExpanded] = useState(false);

  const handleExpand = () => {
    setIsExpanded(true);
    setTimeout(() => {
      inputRef.current?.focus();
    }, 100);
  };

  const handleCollapse = () => {
    setIsExpanded(false);
    setQuery('');
  };

  const handleSearch = () => {
    if (query.trim()) {
      router.push(`/search?q=${encodeURIComponent(query)}`);
      setQuery('');
      setIsExpanded(false);
    }
  };

  return (
    <>
      <div className="lg:hidden flex-1 flex items-center justify-end">
        {!isExpanded ? (
          <Button
            variant="ghost"
            size="icon"
            onClick={handleExpand}
            aria-label="Search"
          >
            <MagnifyingGlassIcon size={20} />
          </Button>
        ) : (
          <div className="flex items-center gap-1 flex-1">
            <InputGroup className="flex-1">
              <InputGroupAddon>
                <MagnifyingGlassIcon />
              </InputGroupAddon>
              <InputGroupInput
                ref={inputRef}
                placeholder={t('search.placeholder')}
                value={query}
                onChange={(e) => setQuery(e.target.value)}
                onKeyDown={(e) => {
                  if (e.key === 'Enter') {
                    e.preventDefault();
                    handleSearch();
                  } else if (e.key === 'Escape') {
                    handleCollapse();
                  }
                }}
              />
            </InputGroup>
            <Button
              variant="ghost"
              size="icon"
              onClick={handleCollapse}
              aria-label="Close search"
            >
              <XIcon size={20} />
            </Button>
          </div>
        )}
      </div>

      <div className="hidden lg:block">
        <InputGroup>
          <InputGroupAddon>
            <MagnifyingGlassIcon />
          </InputGroupAddon>

          <InputGroupInput
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
        </InputGroup>
      </div>
    </>
  );
}
