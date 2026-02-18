import { useQuery } from '@tanstack/react-query';

import { server } from '@/lib/server';
import { ProviderType } from '@/types/provider';
import { PagedResponse } from '@/types/server';
import { url } from '@/util/server';

interface GetProvidersParams {
  query?: string;
  types: ProviderType[];
  cursor: string | null;
  size: number;
}

interface GetProvidersResponse {
  providers: PagedResponse<{
    type: ProviderType;
    id: string;
    name: string;
  }>;
}

async function getProviders(params: GetProvidersParams) {
  return await server
    .get<GetProvidersResponse>(
      url('providers', {
        query: params.query || undefined,
        types: params.types.join(','),
        cursor: params.cursor || undefined,
        size: params.size,
      }),
    )
    .json()
    .then((data) => data.providers);
}

export function useGetProvidersQuery(params: GetProvidersParams) {
  return useQuery({
    queryKey: ['providers', params],
    queryFn: () => getProviders(params),
  });
}
