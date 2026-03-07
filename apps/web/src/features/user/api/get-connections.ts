import { useQuery } from '@tanstack/react-query';

import { server } from '@/lib/server';

interface GetConnectionsResponse {
  connections: {
    userId: string;
    name: string;
    providerName: string | null;
    profession: string | null;
  }[];
}

async function getConnections() {
  return server
    .get<GetConnectionsResponse>('users/connections')
    .json()
    .then((data) =>
      data.connections.map((connection) => ({
        id: connection.userId,
        name: connection.name,
        providerName: connection.providerName,
        profession: connection.profession
      })),
    );
}

export function useGetConnectionsQuery() {
  return useQuery({
    queryKey: ['connections'],
    queryFn: getConnections,
  });
}
