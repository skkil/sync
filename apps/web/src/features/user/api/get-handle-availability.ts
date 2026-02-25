import { useQuery } from '@tanstack/react-query';

import { server } from '@/lib/server';
import { url } from '@/util/server';

import {
  MAXIMUM_HANDLE_LENGTH,
  MINIMUM_HANDLE_LENGTH,
} from '../constants/handle';

interface GetHandleAvailabilityResponse {
  available: boolean;
}

async function getHandleAvailability(handle: string) {
  return server
    .get<GetHandleAvailabilityResponse>(
      url(`handles/availability`, {
        handle,
      }),
    )
    .then((res) => res.json());
}

export function useGetHandleAvailabilityQuery(handle: string) {
  return useQuery({
    queryKey: ['handle-availability', handle],
    queryFn: () => getHandleAvailability(handle),
    enabled:
      handle.length >= MINIMUM_HANDLE_LENGTH &&
      handle.length <= MAXIMUM_HANDLE_LENGTH,
  });
}
