import { useQuery } from '@tanstack/react-query';

import { server } from '@/lib/server';
import { OAuth2Provider } from '@/types/profile';

interface GetOAuth2AccountsResponse {
  accounts: {
    provider: OAuth2Provider;
  }[];
}

async function getOAuth2Accounts() {
  return server
    .get<GetOAuth2AccountsResponse>('users/me/oauth2')
    .json()
    .then((data) => data.accounts);
}

export type GetOAuth2AccountsQueryData = Awaited<
  ReturnType<typeof getOAuth2Accounts>
>;

export function useGetOAuth2AccountsQuery() {
  return useQuery({
    queryKey: ['oauth2-accounts'],
    queryFn: getOAuth2Accounts,
  });
}
