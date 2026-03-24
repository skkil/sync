import { useQuery } from '@tanstack/react-query';

import { server } from '@/lib/server';
import { PagedResponse } from '@/types/server';

interface GetJobPostingsResponse {
  postings: PagedResponse<{
    id: string;
    company: {
      id: string;
      name: string;
    };
    jobTitle: string;
    jobDescription: string;
    location: string;
    createdAt: string;
  }>;
}

async function getJobPostingsByCompany(companyId: string) {
  return server
    .get<GetJobPostingsResponse>(`companies/${companyId}/jobs`)
    .json()
    .then((data) => data.postings);
}

export function useGetJobPostingsByCompanyQuery(companyId: string) {
  return useQuery({
    queryKey: ['company', companyId, 'job-postings'],
    queryFn: () => getJobPostingsByCompany(companyId),
    enabled: !!companyId,
  });
}

async function getJobPostings() {
  return server
    .get<GetJobPostingsResponse>('jobs')
    .json()
    .then((data) => data.postings);
}

export function useGetJobPostingsQuery() {
  return useQuery({
    queryKey: ['job-postings'],
    queryFn: getJobPostings,
  });
}
