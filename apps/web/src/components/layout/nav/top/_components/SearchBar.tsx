'use client';

import { MagnifyingGlassIcon, XIcon } from '@phosphor-icons/react';
import { useTranslations } from 'next-intl';
import { useRouter } from 'next/navigation';
import { useEffect, useRef, useState } from 'react';

import { Button } from '@/components/ui/button';
import {
  InputGroup,
  InputGroupAddon,
  InputGroupInput,
} from '@/components/ui/input-group';

interface SearchBarProps {
  variant: 'desktop' | 'mobile';
}

export default function SearchBar({ variant }: SearchBarProps) {
  const t = useTranslations('components.navigation');
  const router = useRouter();
  const inputRef = useRef<HTMLInputElement>(null);

  const [query, setQuery] = useState('');
  const [isExpanded, setIsExpanded] = useState(false);

  useEffect(() => {
    if (isExpanded) {
      inputRef.current?.focus();
    }
  }, [isExpanded]);

  const handleSearch = () => {
    if (!query.trim()) return;
    router.push(`/search?q=${encodeURIComponent(query.trim())}`);
    setQuery('');
    setIsExpanded(false);
  };

  const handleKeyDown = (e: React.KeyboardEvent<HTMLInputElement>) => {
    if (e.key === 'Enter') {
      e.preventDefault();
      handleSearch();
    } else if (e.key === 'Escape') {
      setIsExpanded(false);
      setQuery('');
    }
  };

  if (variant === 'mobile') {
    return (
      <div className="flex items-center">
        {!isExpanded ? (
          <Button
            variant="ghost"
            size="icon"
            onClick={() => setIsExpanded(true)}
            aria-label="검색"
          >
            <MagnifyingGlassIcon size={20} />
          </Button>
        ) : (
          <div className="flex items-center gap-1">
            <InputGroup>
              <InputGroupAddon>
                <MagnifyingGlassIcon />
              </InputGroupAddon>
              <InputGroupInput
                ref={inputRef}
                placeholder={t('search.placeholder')}
                value={query}
                onChange={(e) => setQuery(e.target.value)}
                onKeyDown={handleKeyDown}
                className="w-40"
              />
            </InputGroup>
            <Button
              variant="ghost"
              size="icon"
              onClick={() => {
                setIsExpanded(false);
                setQuery('');
              }}
              aria-label="검색 닫기"
            >
              <XIcon size={20} />
            </Button>
          </div>
        )}
      </div>
    );
  }

  return (
    <InputGroup>
      <InputGroupAddon>
        <MagnifyingGlassIcon />
      </InputGroupAddon>
      <InputGroupInput
        placeholder={t('search.placeholder')}
        className="w-56"
        value={query}
        onChange={(e) => setQuery(e.target.value)}
        onKeyDown={handleKeyDown}
      />
    </InputGroup>
  );
}
