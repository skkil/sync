'use client';

import { MagnifyingGlassIcon } from '@phosphor-icons/react';
import { useTranslations } from 'next-intl';
import { useSearchParams } from 'next/navigation';
import { useState } from 'react';

import { TwoColumnLayout } from '@/components/layout/TwoColumnLayout';
import {
  Empty,
  EmptyHeader,
  EmptyMedia,
  EmptyTitle,
} from '@/components/ui/empty';

import SearchCategorySidebar, {
  type SearchCategory,
} from './_components/SearchCategorySidebar';
import SearchPostResults from './_components/SearchPostResults';
import SearchProjectResults from './_components/SearchProjectResults';
import SearchQueryInput from './_components/SearchQueryInput';
import SearchTagResults from './_components/SearchTagResults';
import SearchUserResults from './_components/SearchUserResults';

export default function SearchPage() {
  const t = useTranslations('pages.search');
  const searchParams = useSearchParams();
  const query = searchParams.get('q')?.trim() ?? '';
  const projectHandle = searchParams.get('projectHandle')?.trim() || undefined;

  const [category, setCategory] = useState<SearchCategory>('posts');

  if (!query) {
    return (
      <div className="mx-auto max-w-5xl space-y-6 px-4 py-8">
        <SearchQueryInput query={query} projectHandle={projectHandle} />

        <Empty className="min-h-80">
          <EmptyMedia variant="icon">
            <MagnifyingGlassIcon />
          </EmptyMedia>
          <EmptyHeader>
            <EmptyTitle>{t('empty-query')}</EmptyTitle>
          </EmptyHeader>
        </Empty>
      </div>
    );
  }

  return (
    <div className="mx-auto space-y-6 px-4 py-8">
      <h1 className="text-2xl font-semibold">
        {t('results.title', { query })}
      </h1>

      <SearchQueryInput query={query} projectHandle={projectHandle} />

      <TwoColumnLayout
        main={
          <>
            {category === 'posts' && (
              <SearchPostResults query={query} projectHandle={projectHandle} />
            )}
            {category === 'tags' && <SearchTagResults query={query} />}
            {category === 'users' && <SearchUserResults query={query} />}
            {category === 'projects' && <SearchProjectResults query={query} />}
          </>
        }
        side={<SearchCategorySidebar value={category} onChange={setCategory} />}
        reverseSideOnMobile
      />
    </div>
  );
}
