'use client';

import {
  BuildingOfficeIcon,
  BuildingsIcon,
  LightbulbIcon,
  TrophyIcon,
  UserIcon,
} from '@phosphor-icons/react';
import { useTranslations } from 'next-intl';
import Link from 'next/link';
import { useRouter, useSearchParams } from 'next/navigation';
import { useEffect, useMemo, useState } from 'react';

import { Avatar, AvatarFallback } from '@/components/ui/avatar';
import { Badge } from '@/components/ui/badge';
import { Button } from '@/components/ui/button';
import {
  Pagination,
  PaginationContent,
  PaginationItem,
  PaginationLink,
  PaginationNext,
  PaginationPrevious,
} from '@/components/ui/pagination';
import { SearchType, useSearchQuery } from '@/features/search/api/search';
import { url } from '@/util/server';

type SearchCategory = 'user' | 'school' | 'company' | 'contest' | 'project';

const SearchCategories = (
  t: ReturnType<typeof useTranslations>,
): {
  id: SearchCategory;
  type: SearchType;
  urlPrefix: string;
  label: string;
  icon: React.ReactNode;
}[] => [
  {
    id: 'user',
    type: SearchType.USER,
    urlPrefix: 'profile',
    label: t('filters.people'),
    icon: <UserIcon />,
  },
  {
    id: 'school',
    type: SearchType.SCHOOL,
    urlPrefix: 'school',
    label: t('filters.schools'),
    icon: <BuildingsIcon />,
  },
  {
    id: 'company',
    type: SearchType.COMPANY,
    urlPrefix: 'company',
    label: t('filters.companies'),
    icon: <BuildingOfficeIcon />,
  },
  {
    id: 'contest',
    type: SearchType.CONTEST,
    urlPrefix: 'contest',
    label: t('filters.contests'),
    icon: <TrophyIcon />,
  },
  {
    id: 'project',
    type: SearchType.PROJECT,
    urlPrefix: 'project',
    label: t('filters.projects'),
    icon: <LightbulbIcon />,
  },
];

export default function Search() {
  const t = useTranslations('pages.search');
  const router = useRouter();

  const searchParams = useSearchParams();
  const query = searchParams.get('q') || '';

  const categories = useMemo(() => SearchCategories(t), [t]);
  const category = getSearchCategory(searchParams.get('c'));

  const [page, setPage] = useState(0);

  const { data: searchResults } = useSearchQuery({
    query,
    type: categories.find((c) => c.id === category)?.type || SearchType.USER,
    page,
    size: 25,
  });

  useEffect(() => {
    setPage(0);
  }, [query, category]);

  const hasPreviousPage = page > 0;
  const hasNextPage =
    searchResults && page < searchResults.results.page.totalPages - 1;

  const handleCategoryChange = (c: SearchCategory) => {
    router.push(
      url('/search', {
        q: query,
        c,
      }),
    );
  };

  return (
    <div className="mx-auto flex w-full max-w-7xl gap-6 p-6">
      <aside className="w-64 flex-shrink-0">
        <div className="sticky top-6">
          <h2 className="mb-3 px-3 text-sm font-semibold text-muted-foreground">
            {t('filters.title')}
          </h2>
          <nav className="space-y-1">
            {categories.map((c) => {
              const isActive = category === c.id;

              return (
                <Button
                  key={c.id}
                  variant="ghost"
                  onClick={() => handleCategoryChange(c.id)}
                  className={`flex w-full items-center justify-between rounded-md px-3 py-2 text-sm transition-colors ${
                    isActive ? 'bg-muted font-medium' : 'hover:bg-muted/50'
                  }`}
                >
                  <div className="flex items-center gap-2">
                    {c.icon}
                    <span>{c.label}</span>
                  </div>
                  <Badge variant="secondary" className="ml-auto">
                    {searchResults
                      ? getSearchCount(searchResults.count, c.id)
                      : 0}
                  </Badge>
                </Button>
              );
            })}
          </nav>
        </div>
      </aside>

      <main className="min-w-0 flex-1">
        {
          <div className="mb-4">
            {t('results.title', {
              query,
            })}
          </div>
        }

        <div className="min-h-120">
          {searchResults && searchResults.results.content.length > 0 ? (
            searchResults.results.content.map((result) => (
              <Link
                key={result.id}
                href={`/${categories.find((c) => c.id === category)?.urlPrefix}/${result.id}`}
                className="block py-1"
              >
                <div className="rounded-lg border bg-card p-4 transition-colors hover:bg-muted/50">
                  <div className="flex items-start gap-3">
                    <Avatar className="h-10 w-10">
                      <AvatarFallback>
                        {result.name.charAt(0).toUpperCase()}
                      </AvatarFallback>
                    </Avatar>
                    <div className="min-w-0 flex-1">
                      <h3 className="font-semibold text-foreground">
                        {result.name}
                      </h3>
                    </div>
                  </div>
                </div>
              </Link>
            ))
          ) : (
            <div className="rounded-lg border border-dashed p-12 text-center">
              <p className="text-muted-foreground">
                {t('results.no-results', {
                  query,
                })}
              </p>
            </div>
          )}
        </div>

        {searchResults && searchResults.results.page.totalPages > 1 && (
          <Pagination>
            <PaginationContent>
              <PaginationItem>
                <PaginationPrevious
                  className={
                    hasPreviousPage ? '' : 'pointer-events-none opacity-50'
                  }
                  onClick={() => {
                    if (hasPreviousPage) {
                      setPage(page - 1);
                    }
                  }}
                />
              </PaginationItem>

              {Array.from({
                length: searchResults.results.page.totalPages || 0,
              }).map((_, index) => (
                <PaginationItem key={index}>
                  <PaginationLink
                    isActive={page === index}
                    onClick={() => setPage(index)}
                    aria-current={page === index ? 'page' : undefined}
                  >
                    {index + 1}
                  </PaginationLink>
                </PaginationItem>
              ))}

              <PaginationItem>
                <PaginationNext
                  className={
                    hasNextPage ? '' : 'pointer-events-none opacity-50'
                  }
                  onClick={() => {
                    if (hasNextPage) {
                      setPage(page + 1);
                    }
                  }}
                />
              </PaginationItem>
            </PaginationContent>
          </Pagination>
        )}
      </main>
    </div>
  );
}

function getSearchCategory(category: string | null): SearchCategory {
  switch (category) {
    case 'user':
      return 'user';
    case 'school':
      return 'school';
    case 'company':
      return 'company';
    case 'contest':
      return 'contest';
    case 'project':
      return 'project';
    default:
      return 'user';
  }
}

function getSearchCount(
  count: {
    userCount: number;
    schoolCount: number;
    companyCount: number;
    contestCount: number;
    projectCount: number;
  },
  category: SearchCategory,
) {
  switch (category) {
    case 'user':
      return count.userCount;
    case 'school':
      return count.schoolCount;
    case 'company':
      return count.companyCount;
    case 'contest':
      return count.contestCount;
    case 'project':
      return count.projectCount;
    default:
      return 0;
  }
}
