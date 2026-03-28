import { keepPreviousData, useQuery } from '@tanstack/react-query';

import { server } from '@/lib/server';
import { PagedResponse } from '@/types/server';
import { url } from '@/util/server';

export enum SearchType {
  USER = 'USER',
  SCHOOL = 'SCHOOL',
  COMPANY = 'COMPANY',
  CONTEST = 'CONTEST',
  PROJECT = 'PROJECT',
}

interface SearchParams {
  query: string;
  type: SearchType;
  page: number;
  size: number;
}

interface SearchResponse {
  results: PagedResponse<{
    id: string;
    name: string;
  }>;
  count: {
    userCount: number;
    schoolCount: number;
    companyCount: number;
    contestCount: number;
    projectCount: number;
  };
}

async function search(params: SearchParams) {
  return server
    .get<SearchResponse>(
      url('search', {
        ...params,
      }),
    )
    .json();
}

export function useSearchQuery(params: SearchParams) {
  'use no memo';
  return useQuery({
    queryKey: ['search', params],
    queryFn: () => search(params),
    placeholderData: keepPreviousData,
    enabled: params.query.trim().length > 0,
  });
}
