import { useMutation } from '@tanstack/react-query';

import { server } from '@/lib/server';
import { OAuth2Provider } from '@/types/profile';

import { GetOAuth2AccountsQueryData } from './get-oauth2-accounts';

async function deleteOAuth2Account(provider: OAuth2Provider) {
  return server.delete(`users/me/oauth2/${provider}`);
}

export function useDeleteOAuth2AccountMutation() {
  return useMutation({
    mutationFn: deleteOAuth2Account,
    onSuccess: (_data, provider, _onMutateResult, context) => {
      const accounts = context.client.getQueryData<GetOAuth2AccountsQueryData>([
        'oauth2-accounts',
      ]);

      context.client.setQueryData<GetOAuth2AccountsQueryData>(
        ['oauth2-accounts'],
        accounts?.filter((account) => account.provider !== provider),
      );
    },
    onError: (_error, provider, _onMutateResult, context) => {
      const accounts = context.client.getQueryData<GetOAuth2AccountsQueryData>([
        'oauth2-accounts',
      ]);

      context.client.setQueryData<GetOAuth2AccountsQueryData>(
        ['oauth2-accounts'],
        [
          ...(accounts || []),
          {
            provider,
          },
        ],
      );
    },
  });
}
