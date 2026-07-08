'use client';

import {
  BookOpenIcon,
  LightningIcon,
  QuestionIcon,
} from '@phosphor-icons/react';
import { useTranslations } from 'next-intl';

import { cn } from '@/lib/utils';

import { PostType } from '../../types/post';

const TYPE_CONFIG = {
  [PostType.SHORT]: {
    icon: LightningIcon,
    ring: 'has-checked:border-primary has-checked:bg-primary/5',
    dot: 'bg-primary',
    text: 'has-checked:text-primary',
  },
  [PostType.LONG]: {
    icon: BookOpenIcon,
    ring: 'has-checked:border-blue-500 has-checked:bg-blue-500/5',
    dot: 'bg-blue-500',
    text: 'has-checked:text-blue-500',
  },
  [PostType.QUESTION]: {
    icon: QuestionIcon,
    ring: 'has-checked:border-amber-600 has-checked:bg-amber-600/5',
    dot: 'bg-amber-600',
    text: 'has-checked:text-amber-600',
  },
} as const;

interface PostTypeSelectorProps {
  value: PostType;
  onChange: (type: PostType) => void;
}

export function PostTypeSelector({ value, onChange }: PostTypeSelectorProps) {
  const tType = useTranslations('components.post.type');
  const tDescription = useTranslations('components.editor.type-descriptions');

  return (
    <div className="flex flex-col gap-2" role="radiogroup">
      {Object.values(PostType).map((pt) => {
        const config = TYPE_CONFIG[pt];
        const Icon = config.icon;
        const checked = value === pt;

        return (
          <label
            key={pt}
            className={cn(
              'group flex items-start gap-3 rounded-lg border border-border px-3 py-2.5 cursor-pointer transition-colors hover:bg-muted/50',
              config.ring,
            )}
          >
            <input
              type="radio"
              name="post-type"
              className="sr-only"
              checked={checked}
              onChange={() => onChange(pt)}
            />
            <span
              className={cn(
                'mt-0.5 flex size-7 shrink-0 items-center justify-center rounded-md bg-muted text-muted-foreground transition-colors',
                checked && config.dot,
                checked && 'text-white',
              )}
            >
              <Icon size={16} weight={checked ? 'fill' : 'regular'} />
            </span>
            <span className="flex flex-col gap-0.5 min-w-0">
              <span
                className={cn(
                  'text-sm font-semibold text-foreground transition-colors',
                  config.text,
                )}
              >
                {tType(pt)}
              </span>
              <span className="text-xs text-muted-foreground leading-snug">
                {tDescription(pt)}
              </span>
            </span>
          </label>
        );
      })}
    </div>
  );
}
