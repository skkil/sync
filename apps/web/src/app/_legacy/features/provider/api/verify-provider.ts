import { useMutation } from '@tanstack/react-query';

import { server } from '@/lib/server';

async function verifyProvider(id: string) {
  return server.patch<void>(`admin/providers/${id}/verify`);
}

export function useVerifyProviderMutation() {
  return useMutation({
    mutationFn: verifyProvider,
  });
}
