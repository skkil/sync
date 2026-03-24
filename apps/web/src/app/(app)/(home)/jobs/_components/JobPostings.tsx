'use client';

import { MagnifyingGlassIcon } from '@phosphor-icons/react';
import { useTranslations } from 'next-intl';

import { Avatar, AvatarFallback } from '@/components/ui/avatar';
import { Empty, EmptyHeader, EmptyTitle } from '@/components/ui/empty';
import {
  InputGroup,
  InputGroupAddon,
  InputGroupInput,
} from '@/components/ui/input-group';
import { useGetJobPostingsQuery } from '@/features/provider/api/company/get-job-postings';

export default function JobPostings() {
  const t = useTranslations('pages.jobs.postings');
  const { data: postings } = useGetJobPostingsQuery();

  return (
    <>
      <InputGroup className="flex-1">
        <InputGroupAddon>
          <MagnifyingGlassIcon />
        </InputGroupAddon>
        <InputGroupInput />
      </InputGroup>

      <div className="my-2"></div>

      {postings ? (
        <div className="flex flex-col gap-4">
          {postings.content.map((posting) => (
            <div
              key={posting.id}
              className="p-4 flex gap-4 items-center hover:bg-muted"
            >
              <div className="flex items-center gap-2">
                <Avatar>
                  <AvatarFallback />
                </Avatar>

                <span>{posting.company.name}</span>
              </div>

              <div>{posting.jobTitle}</div>
            </div>
          ))}
        </div>
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
