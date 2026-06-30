import { useQuery } from '@tanstack/react-query';

import { server } from '@/lib/server';
import { ProviderType, SchoolType } from '@/types/provider';

type GetProviderResponse = {
  id: string;
  name: string;
  description: string | null;
  contactInfo: string | null;
  createdAt: string;
  updatedAt: string;
  verifiedBy: string | null;
  isMaintainer: boolean;
} & {
  type: ProviderType;
  schoolType?: SchoolType | null;
  industry?: string | null;
};

async function getProvider(id: string) {
  return server.get<GetProviderResponse>(`providers/${id}`).json();
}

export const GetProviderQueryOptions = (id: string) => ({
  queryKey: ['provider', id],
  queryFn: () => getProvider(id),
});

export default function useGetProviderQuery(id: string) {
  return useQuery({
    queryKey: ['provider', id],
    queryFn: () => getProvider(id),
  });
}
