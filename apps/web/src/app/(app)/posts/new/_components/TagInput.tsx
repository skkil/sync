'use client';

import { XIcon } from '@phosphor-icons/react';
import { useDebounce } from '@uidotdev/usehooks';
import { useTranslations } from 'next-intl';
import { type KeyboardEvent, useRef, useState } from 'react';
import { toast } from 'sonner';

import { useSearchTags } from '@/api/__generated__/tag/tag';
import { Badge } from '@/components/ui/badge';
import { cn } from '@/lib/utils';

const MAXIMUM_ALLOWED_TAGS = 5;
const DEBOUNCE_MS = 300;

interface TagInputProps {
  tags: string[];
  onChange: (tags: string[]) => void;
}

export function TagInput({ tags, onChange }: TagInputProps) {
  const t = useTranslations('components.editor.tags');

  const inputRef = useRef<HTMLInputElement>(null);
  const [input, setInput] = useState('');
  const debouncedInput = useDebounce(input, DEBOUNCE_MS);

  const [focused, setFocused] = useState(false);
  const [open, setOpen] = useState(false);

  const { data, isFetching } = useSearchTags(
    { query: debouncedInput },
    {
      query: {
        enabled: debouncedInput.trim().length > 0,
      },
    },
  );

  const suggestions = data?.data?.tags ?? [];
  const showDropdown = open && focused && input.trim().length > 0;

  const isPending = input !== debouncedInput || isFetching;

  const addTag = (name: string) => {
    if (tags.length >= MAXIMUM_ALLOWED_TAGS) {
      toast.error(
        t('max-tags', {
          count: MAXIMUM_ALLOWED_TAGS,
        }),
      );
      return;
    }

    const trimmed = name.trim().toLowerCase();
    if (!trimmed || tags.includes(trimmed)) {
      return;
    }

    onChange([...tags, trimmed]);
    setInput('');
    setOpen(false);
  };

  const removeTag = (name: string) => {
    onChange(tags.filter((t) => t !== name));
  };

  const handleKeyDown = (e: KeyboardEvent<HTMLInputElement>) => {
    if (e.key === 'Enter' || e.key === ',') {
      e.preventDefault();
      addTag(input);
    } else if (e.key === 'Backspace' && input === '' && tags.length > 0) {
      const last = tags[tags.length - 1];
      if (last) removeTag(last);
    } else if (e.key === 'Escape') {
      setOpen(false);
    }
  };

  return (
    <div className="relative flex flex-col gap-2">
      <div
        className="flex flex-wrap items-center gap-1.5 rounded-md border border-input bg-transparent px-3 py-2 text-sm cursor-text min-h-9"
        onClick={() => inputRef.current?.focus()}
      >
        {tags.map((tag) => (
          <button
            key={tag}
            onClick={(e) => {
              e.stopPropagation();
              removeTag(tag);
            }}
          >
            <Badge className="inline-flex items-center gap-1 rounded px-2 py-0.5 text-lg font-medium">
              {tag}
              <XIcon size={10} />
            </Badge>
          </button>
        ))}

        <input
          ref={inputRef}
          value={input}
          onChange={(e) => {
            setInput(e.target.value);
            setOpen(true);
          }}
          onKeyDown={handleKeyDown}
          onFocus={() => {
            setFocused(true);
            if (input.length > 0) setOpen(true);
          }}
          onBlur={() => {
            setFocused(false);
            setTimeout(() => setOpen(false), 150);
          }}
          placeholder={tags.length === 0 ? t('placeholder') : ''}
          className="flex-1 min-w-24 bg-transparent outline-none placeholder:text-muted-foreground text-lg"
        />
      </div>

      {showDropdown && (
        <div className="absolute top-full left-0 right-0 z-50 mt-1 rounded-md border bg-popover shadow-md overflow-hidden min-h-10">
          {isPending ? (
            <div className="flex items-center justify-center px-3 py-3 text-sm text-muted-foreground">
              {t('loading')}
            </div>
          ) : suggestions.length === 0 ? (
            <div className="flex items-center justify-center px-3 py-3 text-sm text-muted-foreground">
              {t('not-found')}
            </div>
          ) : (
            suggestions.map((tag) => (
              <button
                key={tag.name}
                type="button"
                onMouseDown={(e) => {
                  e.preventDefault();
                  addTag(tag.name);
                }}
                className={cn(
                  'w-full flex items-center justify-between px-3 py-2 text-sm hover:bg-accent text-left transition-colors',
                  tags.includes(tag.name) && 'opacity-50 cursor-not-allowed',
                )}
              >
                <span className="font-medium">{tag.name}</span>
                <span className="text-xs text-muted-foreground">
                  {t('post-count', { count: tag.postCount })}
                </span>
              </button>
            ))
          )}
        </div>
      )}
    </div>
  );
}
