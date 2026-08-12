'use client';

import { ListNumbersIcon, PlusIcon, XIcon } from '@phosphor-icons/react';
import { useTranslations } from 'next-intl';
import { useMemo, useState } from 'react';

import {
  Popover,
  PopoverAnchor,
  PopoverContent,
} from '@/components/ui/popover';
import { cn } from '@/lib/utils';

import { usePostSeriesList } from '../../hooks/usePostSeriesList';

export type SeriesSelection =
  | { kind: 'existing'; externalId: string; name: string }
  | { kind: 'new'; name: string };

interface SeriesSelectProps {
  value: SeriesSelection | null;
  onChange: (value: SeriesSelection | null) => void;
  projectHandle?: string;
  accentRing?: string;
}

/**
 * 게시글이 속할 시리즈를 하나 고르는 단일 선택 콤보박스. 기존 시리즈를 검색해 고르거나,
 * 입력한 이름으로 새 시리즈를 만들 수 있다(한 게시글은 최대 하나의 시리즈에만 속한다).
 * 실제 편성은 저장 시점에 처리되므로 여기서는 선택 상태만 관리한다.
 */
export function SeriesSelect({
  value,
  onChange,
  projectHandle,
  accentRing,
}: SeriesSelectProps) {
  const t = useTranslations('components.editor.series');
  const [query, setQuery] = useState('');
  const [open, setOpen] = useState(false);
  const { series, isPending } = usePostSeriesList({ projectHandle });

  const trimmed = query.trim();
  const filtered = useMemo(() => {
    const needle = trimmed.toLowerCase();
    return series.filter((item) => item.name.toLowerCase().includes(needle));
  }, [series, trimmed]);

  const showCreate =
    trimmed.length > 0 &&
    !series.some((item) => item.name.toLowerCase() === trimmed.toLowerCase());

  if (value) {
    return (
      <div className="flex items-center gap-2 rounded-lg border border-border bg-background px-3 py-2.5">
        <span className="flex size-7 shrink-0 items-center justify-center rounded-md bg-muted text-muted-foreground">
          <ListNumbersIcon size={16} />
        </span>
        <span className="truncate text-sm font-medium">{value.name}</span>
        {value.kind === 'new' && (
          <span className="shrink-0 rounded bg-muted px-1.5 py-0.5 text-xs text-muted-foreground">
            {t('new-badge')}
          </span>
        )}
        <button
          type="button"
          aria-label={t('clear')}
          className="ml-auto shrink-0 rounded-md p-1 text-muted-foreground hover:bg-muted hover:text-foreground"
          onClick={() => onChange(null)}
        >
          <XIcon size={16} />
        </button>
      </div>
    );
  }

  const selectExisting = (externalId: string, name: string) => {
    onChange({ kind: 'existing', externalId, name });
    setQuery('');
    setOpen(false);
  };

  const createNew = () => {
    onChange({ kind: 'new', name: trimmed });
    setQuery('');
    setOpen(false);
  };

  return (
    <Popover open={open && (trimmed.length > 0 || filtered.length > 0)}>
      <PopoverAnchor asChild>
        <input
          value={query}
          placeholder={t('placeholder')}
          onChange={(event) => {
            setQuery(event.target.value);
            setOpen(true);
          }}
          onFocus={() => setOpen(true)}
          onBlur={() => setOpen(false)}
          className={cn(
            'w-full rounded-lg border border-border bg-background px-3 py-2.5 text-sm outline-none focus:ring-2',
            accentRing ?? 'focus:ring-primary/30',
          )}
        />
      </PopoverAnchor>

      <PopoverContent
        align="start"
        className="max-h-60 w-(--radix-popover-trigger-width) gap-0 overflow-y-auto rounded-lg p-0 py-1"
        onOpenAutoFocus={(event) => event.preventDefault()}
        onMouseDown={(event) => event.preventDefault()}
      >
        <ul>
          {filtered.map((item) => (
            <li key={item.externalId}>
              <button
                type="button"
                className="flex w-full items-center justify-between gap-2 px-3 py-2 text-left text-sm hover:bg-accent"
                onClick={() => selectExisting(item.externalId, item.name)}
              >
                <span className="truncate">{item.name}</span>
                <span className="shrink-0 text-xs text-muted-foreground">
                  {t('post-count', { count: item.postCount })}
                </span>
              </button>
            </li>
          ))}

          {showCreate && (
            <li>
              <button
                type="button"
                className="flex w-full items-center gap-2 px-3 py-2 text-left text-sm hover:bg-accent"
                onClick={createNew}
              >
                <PlusIcon size={16} className="shrink-0" />
                <span className="truncate">
                  {t('create', { name: trimmed })}
                </span>
              </button>
            </li>
          )}

          {isPending && filtered.length === 0 && (
            <li className="px-3 py-2 text-sm text-muted-foreground">
              {t('loading')}
            </li>
          )}

          {!isPending && filtered.length === 0 && !showCreate && (
            <li className="px-3 py-2 text-sm text-muted-foreground">
              {t('empty')}
            </li>
          )}
        </ul>
      </PopoverContent>
    </Popover>
  );
}
