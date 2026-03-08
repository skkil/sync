import { useQuery } from '@tanstack/react-query';

import { server } from '@/lib/server';

interface GetJobPostingsResponse {
  postings: {
    id: string;
    jobTitle: string;
    jobDescription: string;
    location: string;
    createdAt: string;
  }[];
}

async function getJobPostings(companyId: string) {
  return server
    .get<GetJobPostingsResponse>(`companies/${companyId}/jobs`)
    .json()
    .then((data) => data.postings);
}

export function useGetJobPostingsQuery(companyId: string) {
  return useQuery({
    queryKey: ['company', companyId, 'job-postings'],
    queryFn: () => getJobPostings(companyId),
    enabled: !!companyId,
  });
}
