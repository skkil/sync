import { useMutation } from '@tanstack/react-query';

import { server } from '@/lib/server';
import { ProviderType, SchoolType } from '@/types/provider';

type CreateProviderRequest = {
  name: string;
  description: string;
} & (
  | {
      type: ProviderType.COMPANY;
      industry: string;
    }
  | {
      type: ProviderType.SCHOOL;
      schoolType: SchoolType;
    }
  | {
      type: ProviderType.CONTEST;
      hostProviderId?: string;
    }
  | {
      type: ProviderType.PROJECT;
    }
);
type CreateProviderResponse = {
  id: number;
};

async function createProvider(request: CreateProviderRequest) {
  return server
    .post<CreateProviderResponse>('providers', {
      json: request,
    })
    .json();
}

export function useCreateProviderMutation() {
  return useMutation({
    mutationFn: createProvider,
  });
}
