import { useQuery } from '@tanstack/react-query';

import { server } from '@/lib/server';
import { PagedResponse } from '@/types/server';
import { url } from '@/util/server';

interface GetUnverifiedProvidersResponse {
  providers: PagedResponse<{
    id: string;
    name: string;
  }>;
}

async function getUnverifiedProviders(page: number, pageSize: number) {
  return server
    .get<GetUnverifiedProvidersResponse>(
      url('admin/providers/unverified', {
        page: page.toString(),
        size: pageSize.toString(),
      }),
    )
    .json()
    .then((data) => data.providers);
}

export function useGetUnverifiedProvidersQuery(page: number, pageSize: number) {
  return useQuery({
    queryKey: ['unverified-providers', page, pageSize],
    queryFn: () => getUnverifiedProviders(page, pageSize),
  });
}
