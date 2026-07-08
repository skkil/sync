'use client';

import {
  FileTextIcon,
  FolderSimpleIcon,
  HashIcon,
  UsersIcon,
} from '@phosphor-icons/react';
import { useTranslations } from 'next-intl';

import { Tabs, TabsList, TabsTrigger } from '@/components/ui/tabs';
import { cn } from '@/lib/utils';

export type SearchCategory = 'posts' | 'tags' | 'users' | 'projects';

const CATEGORIES: { value: SearchCategory; icon: typeof FileTextIcon }[] = [
  { value: 'posts', icon: FileTextIcon },
  { value: 'tags', icon: HashIcon },
  { value: 'users', icon: UsersIcon },
  { value: 'projects', icon: FolderSimpleIcon },
];

interface SearchCategorySidebarProps {
  value: SearchCategory;
  onChange: (value: SearchCategory) => void;
}

export default function SearchCategorySidebar({
  value,
  onChange,
}: SearchCategorySidebarProps) {
  const t = useTranslations('pages.search');

  return (
    <>
      <Tabs
        value={value}
        onValueChange={(next) => onChange(next as SearchCategory)}
        className="lg:hidden"
      >
        <TabsList variant="line">
          {CATEGORIES.map((category) => (
            <TabsTrigger key={category.value} value={category.value}>
              {t(`tabs.${category.value}`)}
            </TabsTrigger>
          ))}
        </TabsList>
      </Tabs>

      <div className="hidden flex-col gap-1 lg:flex">
        {CATEGORIES.map((category) => {
          const Icon = category.icon;
          const isActive = value === category.value;

          return (
            <button
              key={category.value}
              type="button"
              onClick={() => onChange(category.value)}
              className={cn(
                'flex items-center gap-2 rounded-md px-3 py-2 text-left text-sm font-medium transition-colors',
                isActive
                  ? 'bg-muted text-foreground'
                  : 'text-muted-foreground hover:bg-muted/50 hover:text-foreground',
              )}
            >
              <Icon className="size-4" />
              {t(`tabs.${category.value}`)}
            </button>
          );
        })}
      </div>
    </>
  );
}
