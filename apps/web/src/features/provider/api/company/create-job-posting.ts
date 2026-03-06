import { useMutation } from '@tanstack/react-query';

import { server } from '@/lib/server';

type CreateJobPostingRequest = {
  jobTitle: string;
  jobDescription: string;
  location?: string;
};

type CreateJobPostingResponse = {
  id: string;
};

async function createJobPosting(
  companyId: string,
  request: CreateJobPostingRequest,
) {
  return server
    .post<CreateJobPostingResponse>(`companies/${companyId}/jobs`, {
      json: request,
    })
    .json();
}

export function useCreateJobPostingMutation(companyId: string) {
  return useMutation({
    mutationFn: (request: CreateJobPostingRequest) =>
      createJobPosting(companyId, request),
  });
}
