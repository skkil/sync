'use client';

import { useTranslations } from 'next-intl';
import Link from 'next/link';
import { useState } from 'react';

import { useGetMyJobApplications } from '@/api/__generated__/applications/applications';
import { Avatar, AvatarFallback } from '@/components/ui/avatar';
import { Empty, EmptyHeader, EmptyTitle } from '@/components/ui/empty';
import {
  Pagination,
  PaginationContent,
  PaginationItem,
  PaginationNext,
  PaginationPrevious,
} from '@/components/ui/pagination';

const APPLICATIONS_PAGE_SIZE = 25;

export default function MyApplications() {
  const t = useTranslations('pages.profile.applications');
  const [page, setPage] = useState(0);

  const { data, isLoading, isError } = useGetMyJobApplications({
    page: page.toString(),
    size: APPLICATIONS_PAGE_SIZE.toString(),
  });

  const applications = data?.data?.applications;
  const hasPrevious = applications?.hasPrevious ?? false;
  const hasNext = applications?.hasNext ?? false;
  const showPagination = applications && (hasPrevious || hasNext);

  return (
    <>
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
      ) : applications && applications.content.length > 0 ? (
        <>
          <div className="flex flex-col gap-4">
            {applications.content.map((application) => (
              <Link
                href={`/profile/me/applications/${application.id}`}
                key={application.id}
              >
                <div className="p-4 flex gap-4 items-center hover:bg-muted rounded-lg border bg-card transition-colors">
                  <div className="flex items-center gap-2">
                    <Avatar>
                      <AvatarFallback></AvatarFallback>
                    </Avatar>

                    <span>{application.company?.name}</span>
                  </div>
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
