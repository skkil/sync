'use client';

import type { BaseUIEvent } from '@base-ui/react/types';
import { PlusIcon } from '@phosphor-icons/react';
import { useTranslations } from 'next-intl';
import {
  type ClipboardEvent,
  type KeyboardEvent,
  type FocusEvent as ReactFocusEvent,
  forwardRef,
  useEffect,
  useImperativeHandle,
  useRef,
  useState,
} from 'react';
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

import {
  MAXIMUM_ALLOWED_TAGS,
  appendTagNames,
  normalizeTagName,
  parseDelimitedTagInput,
  parsePastedTagInput,
} from './tagInputUtils';

// 표준 HTML 토큰과 내부 DOM 연결 표식은 번역하지 않는다.
const TAG_ENTER_KEY_HINT = 'enter' as const;
const TAG_COMMIT_TARGET_SELECTOR = '[data-tag-commit-target]';

export interface TagValue {
  name: string;
  isProjectTag: boolean;
}

interface TagOption extends TagValue {
  postCount?: number;
  isCreate?: boolean;
}

interface TagInputProps {
  tags: TagValue[];
  onChange: (tags: TagValue[]) => void;
  accentRing?: string;
  projectHandle?: string;
}

export interface TagInputHandle {
  commitPending: () => boolean;
}

export const TagInput = forwardRef<TagInputHandle, TagInputProps>(
  function TagInput(
    { tags, onChange, accentRing, projectHandle }: TagInputProps,
    ref,
  ) {
    const t = useTranslations('components.editor.tags');
    const anchor = useComboboxAnchor();

    const [inputValue, setInputValue] = useState('');
    const inputElementRef = useRef<HTMLInputElement>(null);
    const inputValueRef = useRef('');
    const pendingInputHandledRef = useRef(true);
    const tagsRef = useRef(tags);
    const highlightedItemRef = useRef<TagOption | undefined>(undefined);

    useEffect(() => {
      tagsRef.current = tags;
    }, [tags]);

    const {
      setQuery,
      tags: searchedTags,
      isPending,
    } = useTagSearch({ handle: projectHandle });

    const updateInputValue = (value: string) => {
      pendingInputHandledRef.current = value.length === 0;
      inputValueRef.current = value;
      setInputValue(value);
      setQuery(value);
    };

    const suggestions: TagOption[] = isPending
      ? []
      : searchedTags.map((tag) => ({
          name: tag.name,
          isProjectTag: tag.projectHandle != null,
          postCount: tag.postCount,
        }));

    const applyTags = (nextTags: TagValue[]) => {
      tagsRef.current = nextTags;
      onChange(nextTags);
    };

    const resolveTag = (name: string): TagValue => {
      const exactSuggestions = suggestions.filter(
        (tag) => normalizeTagName(tag.name) === name,
      );
      const preferredProjectTag = !!projectHandle;
      const exactSuggestion =
        exactSuggestions.find(
          (tag) => tag.isProjectTag === preferredProjectTag,
        ) ?? exactSuggestions[0];

      return {
        name,
        isProjectTag: exactSuggestion?.isProjectTag ?? preferredProjectTag,
      };
    };

    const commitTagNames = (rawNames: readonly string[], remainder = '') => {
      const currentTags = tagsRef.current;
      const result = appendTagNames(currentTags, rawNames, resolveTag);

      if (result.tags.length !== currentTags.length) {
        applyTags(result.tags);
      }

      const normalizedRemainder = normalizeTagName(remainder);
      const remainderExceedsLimit =
        normalizedRemainder.length > 0 &&
        result.tags.length >= MAXIMUM_ALLOWED_TAGS &&
        !result.tags.some(
          (tag) => normalizeTagName(tag.name) === normalizedRemainder,
        );
      const nextInput = remainderExceedsLimit ? '' : remainder;
      updateInputValue(nextInput);

      if (result.limitExceeded || remainderExceedsLimit) {
        toast.error(
          t('max-tags', {
            count: MAXIMUM_ALLOWED_TAGS,
          }),
        );
      }

      return !result.limitExceeded && !remainderExceedsLimit;
    };

    const commitPendingInput = () => {
      if (pendingInputHandledRef.current) {
        return true;
      }

      return commitTagNames([
        inputElementRef.current?.value ?? inputValueRef.current,
      ]);
    };

    useImperativeHandle(ref, () => ({ commitPending: commitPendingInput }));

    const handleKeyDown = (e: BaseUIEvent<KeyboardEvent<HTMLInputElement>>) => {
      if (e.key !== 'Enter') {
        return;
      }

      if (e.nativeEvent.isComposing || e.keyCode === 229) {
        e.preventBaseUIHandler();
        return;
      }

      if (highlightedItemRef.current) {
        return;
      }

      e.preventDefault();
      e.preventBaseUIHandler();
      commitTagNames([e.currentTarget.value]);
    };

    const handlePaste = (e: BaseUIEvent<ClipboardEvent<HTMLInputElement>>) => {
      const input = e.currentTarget;
      const pastedValue = e.clipboardData.getData('text');
      const selectionStart = input.selectionStart ?? input.value.length;
      const selectionEnd = input.selectionEnd ?? selectionStart;
      const nextValue =
        input.value.slice(0, selectionStart) +
        pastedValue +
        input.value.slice(selectionEnd);
      const pastedTagNames = parsePastedTagInput(nextValue);

      if (!pastedTagNames) {
        return;
      }

      e.preventDefault();
      e.preventBaseUIHandler();
      commitTagNames(pastedTagNames);
    };

    const handleBlur = (e: ReactFocusEvent<HTMLInputElement>) => {
      highlightedItemRef.current = undefined;

      if (
        e.relatedTarget instanceof Element &&
        e.relatedTarget.closest(TAG_COMMIT_TARGET_SELECTOR)
      ) {
        commitPendingInput();
        return;
      }

      updateInputValue('');
    };

    const normalizedInput = normalizeTagName(inputValue);
    const hasExactSuggestion = suggestions.some(
      (tag) => normalizeTagName(tag.name) === normalizedInput,
    );
    const hasSelectedTag = tags.some(
      (tag) => normalizeTagName(tag.name) === normalizedInput,
    );
    const showCreate =
      !isPending &&
      normalizedInput.length > 0 &&
      !hasExactSuggestion &&
      !hasSelectedTag &&
      tags.length < MAXIMUM_ALLOWED_TAGS;
    const options: TagOption[] = showCreate
      ? [
          ...suggestions,
          {
            name: normalizedInput,
            isProjectTag: !!projectHandle,
            isCreate: true,
          },
        ]
      : suggestions;

    return (
      <div className="flex flex-col gap-1.5">
        <Combobox
          items={options}
          multiple
          value={tags}
          onValueChange={(next) => {
            const normalizedNext = next.map(({ name, isProjectTag }) => ({
              name,
              isProjectTag,
            }));
            const currentTags = tagsRef.current;

            if (normalizedNext.length <= currentTags.length) {
              applyTags(normalizedNext);
              return;
            }

            if (normalizedNext.length > MAXIMUM_ALLOWED_TAGS) {
              toast.error(
                t('max-tags', {
                  count: MAXIMUM_ALLOWED_TAGS,
                }),
              );
              return;
            }

            applyTags(normalizedNext);
            updateInputValue('');
          }}
          inputValue={inputValue}
          onInputValueChange={(value, eventDetails) => {
            if (eventDetails.reason !== 'input-change') {
              updateInputValue(value);
              return;
            }

            const parsed = parseDelimitedTagInput(value);
            if (!parsed) {
              updateInputValue(value);
              return;
            }

            eventDetails.cancel();
            commitTagNames(parsed.completedValues, parsed.remainder);
          }}
          onOpenChange={(open) => {
            if (open) {
              return;
            }

            highlightedItemRef.current = undefined;
            updateInputValue('');
          }}
          onItemHighlighted={(item) => {
            highlightedItemRef.current = item;
          }}
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
                    applyTags(
                      tagsRef.current.filter(
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
              ref={inputElementRef}
              aria-label={t('input-label')}
              disabled={tags.length >= MAXIMUM_ALLOWED_TAGS}
              enterKeyHint={TAG_ENTER_KEY_HINT}
              inputMode="text"
              placeholder={tags.length === 0 ? t('placeholder') : ''}
              onBlur={handleBlur}
              onCompositionStart={() => {
                pendingInputHandledRef.current = false;
              }}
              onKeyDown={handleKeyDown}
              onPaste={handlePaste}
            />
          </ComboboxChips>

          <ComboboxContent anchor={anchor}>
            <ComboboxList>
              <ComboboxCollection>
                {(item: TagOption) => (
                  <ComboboxItem
                    key={`${item.isCreate ? 'create' : 'existing'}:${item.isProjectTag}:${item.name}`}
                    value={item}
                    className={item.isCreate ? 'min-h-11' : undefined}
                  >
                    {item.isCreate ? (
                      <>
                        <PlusIcon className="shrink-0" />
                        <span className="truncate">
                          {t('create', { name: item.name })}
                        </span>
                      </>
                    ) : (
                      <>
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
                      </>
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

        <p className="px-1 text-xs text-muted-foreground">{t('helper')}</p>
      </div>
    );
  },
);
