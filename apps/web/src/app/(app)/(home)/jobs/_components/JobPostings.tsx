'use client';

import { MagnifyingGlassIcon } from '@phosphor-icons/react';
import { useTranslations } from 'next-intl';
import Link from 'next/link';
import { useState } from 'react';

import { useGetJobPostings } from '@/api/__generated__/jobs/jobs';
import { Avatar, AvatarFallback } from '@/components/ui/avatar';
import { Empty, EmptyHeader, EmptyTitle } from '@/components/ui/empty';
import {
  InputGroup,
  InputGroupAddon,
  InputGroupInput,
} from '@/components/ui/input-group';
import {
  Pagination,
  PaginationContent,
  PaginationItem,
  PaginationNext,
  PaginationPrevious,
} from '@/components/ui/pagination';

const JOB_POSTINGS_PAGE_SIZE = 25;

export default function JobPostings() {
  const t = useTranslations('pages.jobs.postings');
  const [page, setPage] = useState(0);

  const { data, isLoading, isError } = useGetJobPostings({
    page: page.toString(),
    size: JOB_POSTINGS_PAGE_SIZE.toString(),
  });

  const postings = data?.data?.postings;
  const hasPrevious = postings?.hasPrevious ?? false;
  const hasNext = postings?.hasNext ?? false;
  const showPagination = postings && (hasPrevious || hasNext);

  return (
    <>
      <InputGroup className="flex-1">
        <InputGroupAddon>
          <MagnifyingGlassIcon />
        </InputGroupAddon>
        <InputGroupInput />
      </InputGroup>

      <div className="my-2"></div>

      {isLoading ? (
        <Empty>
          <EmptyHeader>
            <EmptyTitle>{t('loading')}</EmptyTitle>
          </EmptyHeader>
        </Empty>
      ) : isError ? (
        <Empty>
          <EmptyHeader>
            <EmptyTitle>{t('error')}</EmptyTitle>
          </EmptyHeader>
        </Empty>
      ) : postings && postings.content.length > 0 ? (
        <>
          <div className="flex flex-col gap-4">
            {postings.content.map((posting) => (
              <Link
                key={posting.id}
                href={`/company/${posting.company.id}/jobs/${posting.id}?m=true`}
              >
                <div className="p-4 flex gap-4 items-center hover:bg-muted rounded-lg border bg-card transition-colors">
                  <div className="flex items-center gap-2">
                    <Avatar>
                      <AvatarFallback>
                        {posting.company.name?.charAt(0).toUpperCase() || 'C'}
                      </AvatarFallback>
                    </Avatar>

                    <span>{posting.company.name}</span>
                  </div>

                  <div>{posting.jobTitle}</div>
                </div>
              </Link>
            ))}
          </div>

          {showPagination && (
            <div className="mt-6">
              <Pagination>
                <PaginationContent>
                  <PaginationItem>
                    <PaginationPrevious
                      disabled={!hasPrevious}
                      onClick={() => {
                        if (hasPrevious) {
                          setPage(page - 1);
                        }
                      }}
                    />
                  </PaginationItem>

                  <PaginationItem>
                    <PaginationNext
                      disabled={!hasNext}
                      onClick={() => {
                        if (hasNext) {
                          setPage(page + 1);
                        }
                      }}
                    />
                  </PaginationItem>
                </PaginationContent>
              </Pagination>
            </div>
          )}
        </>
      ) : (
        <Empty>
          <EmptyHeader>
            <EmptyTitle>{t('not-found')}</EmptyTitle>
          </EmptyHeader>
        </Empty>
      )}
    </>
  );
}
