import { useQuery } from '@tanstack/react-query';

import { server } from '@/lib/server';

interface GetConnectionsResponse {
  connections: {
    userId: string;
    name: string;
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
      })),
    );
}

export function useGetConnectionsQuery() {
  return useQuery({
    queryKey: ['connections'],
    queryFn: getConnections,
  });
}
