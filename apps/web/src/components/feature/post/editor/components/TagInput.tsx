'use client';

import { useTranslations } from 'next-intl';
import { type KeyboardEvent } from 'react';
import { toast } from 'sonner';

import { RemovableTagBadge, TagBadge } from '@/components/feature/tag/TagBadge';
import { useTagSearch } from '@/components/feature/tag/hooks/useTagSearch';
import {
  Combobox,
  ComboboxChip,
  ComboboxChips,
  ComboboxChipsInput,
  ComboboxCollection,
  ComboboxContent,
  ComboboxEmpty,
  ComboboxItem,
  ComboboxList,
  useComboboxAnchor,
} from '@/components/ui/combobox';
import { cn } from '@/lib/utils';

const MAXIMUM_ALLOWED_TAGS = 5;

export interface TagValue {
  name: string;
  isProjectTag: boolean;
}

interface TagOption extends TagValue {
  postCount?: number;
}

interface TagInputProps {
  tags: TagValue[];
  onChange: (tags: TagValue[]) => void;
  accentRing?: string;
  projectHandle?: string;
}

export function TagInput({
  tags,
  onChange,
  accentRing,
  projectHandle,
}: TagInputProps) {
  const t = useTranslations('components.editor.tags');
  const anchor = useComboboxAnchor();

  const {
    query: inputValue,
    setQuery: setInputValue,
    tags: searchedTags,
    isPending,
  } = useTagSearch({ handle: projectHandle });

  const suggestions: TagOption[] = isPending
    ? []
    : searchedTags.map((tag) => ({
        name: tag.name,
        isProjectTag: tag.projectHandle != null,
        postCount: tag.postCount,
      }));

  const tagNames = tags.map((tag) => tag.name);

  const addTag = (name: string, isProjectTag: boolean) => {
    if (tags.length >= MAXIMUM_ALLOWED_TAGS) {
      toast.error(
        t('max-tags', {
          count: MAXIMUM_ALLOWED_TAGS,
        }),
      );
      return;
    }

    const trimmed = name.trim().toLowerCase();
    if (!trimmed || tagNames.includes(trimmed)) {
      return;
    }

    onChange([...tags, { name: trimmed, isProjectTag }]);
    setInputValue('');
  };

  const handleKeyDown = (e: KeyboardEvent<HTMLInputElement>) => {
    if (e.key === 'Enter' || e.key === ',') {
      e.preventDefault();
      addTag(inputValue, !!projectHandle);
    }
  };

  return (
    <Combobox
      items={suggestions}
      multiple
      value={tags}
      onValueChange={(next) => {
        if (next.length <= tags.length) {
          onChange(next);
          return;
        }

        if (next.length > MAXIMUM_ALLOWED_TAGS) {
          toast.error(
            t('max-tags', {
              count: MAXIMUM_ALLOWED_TAGS,
            }),
          );
          return;
        }

        onChange(next);
        setInputValue('');
      }}
      inputValue={inputValue}
      onInputValueChange={setInputValue}
      isItemEqualToValue={(item, value) =>
        item.name === value.name && item.isProjectTag === value.isProjectTag
      }
      itemToStringLabel={(item) => item.name}
      filter={null}
    >
      <ComboboxChips
        ref={anchor}
        className={cn(
          'focus-within:ring-2',
          accentRing ?? 'focus-within:ring-primary/30',
        )}
      >
        {tags.map((tag) => (
          <ComboboxChip
            key={`${tag.isProjectTag}:${tag.name}`}
            showRemove={false}
            className="bg-transparent px-0"
          >
            <RemovableTagBadge
              name={tag.name}
              isProjectTag={tag.isProjectTag}
              variant="secondary"
              onRemove={() =>
                onChange(
                  tags.filter(
                    (t) =>
                      !(
                        t.name === tag.name &&
                        t.isProjectTag === tag.isProjectTag
                      ),
                  ),
                )
              }
            />
          </ComboboxChip>
        ))}

        <ComboboxChipsInput
          placeholder={tags.length === 0 ? t('placeholder') : ''}
          onKeyDown={handleKeyDown}
        />
      </ComboboxChips>

      <ComboboxContent anchor={anchor}>
        <ComboboxList>
          <ComboboxCollection>
            {(item: TagOption) => (
              <ComboboxItem key={item.name} value={item}>
                <TagBadge
                  name={item.name}
                  isProjectTag={item.isProjectTag}
                  variant="secondary"
                />
                {item.postCount !== undefined && (
                  <span className="text-xs text-muted-foreground">
                    {t('post-count', { count: item.postCount })}
                  </span>
                )}
              </ComboboxItem>
            )}
          </ComboboxCollection>
          <ComboboxEmpty>
            {isPending ? t('loading') : t('not-found')}
          </ComboboxEmpty>
        </ComboboxList>
      </ComboboxContent>
    </Combobox>
  );
}
