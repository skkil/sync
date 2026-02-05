import { useQuery } from '@tanstack/react-query';

import { server } from '@/lib/server';
import { ProviderType } from '@/types/provider';
import { url } from '@/util/server';

interface GetProvidersParams {
  type: ProviderType;
}

interface GetProvidersResponse {
  providers: {
    id: string;
    name: string;
  }[];
}

async function getProviders({ type }: GetProvidersParams) {
  return await server
    .get<GetProvidersResponse>(
      url('providers', {
        type,
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
