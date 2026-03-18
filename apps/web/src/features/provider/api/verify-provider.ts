import { QueryClient, useMutation } from '@tanstack/react-query';

import { server } from '@/lib/server';
import { PagedResponse } from '@/types/server';

type UnverifiedProvider = {
  id: string;
  name: string;
};

type UnverifiedProvidersPage = PagedResponse<UnverifiedProvider>;

async function verifyProvider(id: string) {
  return server.patch<void>(`admin/providers/${id}/verify`);
}

function removeVerifiedProviderFromPage(
  page: UnverifiedProvidersPage | undefined,
  providerId: string,
) {
  if (!page) {
    return page;
  }

  const content = page.content.filter((provider) => provider.id !== providerId);

  if (content.length === page.content.length) {
    return page;
  }

  const totalElements = Math.max(0, page.page.totalElements - 1);
  const totalPages =
    totalElements === 0 ? 0 : Math.ceil(totalElements / page.page.size);

  return {
    ...page,
    content,
    page: {
      ...page.page,
      totalElements,
      totalPages,
    },
  };
}

function removeVerifiedProviderFromCache(
  queryClient: QueryClient,
  providerId: string,
) {
  const queries = queryClient.getQueriesData<UnverifiedProvidersPage>({
    queryKey: ['unverified-providers'],
  });

  queries.forEach(([queryKey, page]) => {
    queryClient.setQueryData(
      queryKey,
      removeVerifiedProviderFromPage(page, providerId),
    );
  });
}

export function useVerifyProviderMutation() {
  return useMutation({
    mutationFn: verifyProvider,
    onSuccess: async (_data, providerId, _onMutateResult, context) => {
      removeVerifiedProviderFromCache(context.client, providerId);

      await context.client.invalidateQueries({
        queryKey: ['unverified-providers'],
      });
    },
  });
}
