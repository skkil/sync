import { useMutation } from '@tanstack/react-query';

import { server } from '@/lib/server/client';

interface LoginRequest {
  email: string;
  password: string;
}

function login(request: LoginRequest) {
  return server.post<void>('auth/login', {
    json: request,
  });
}

export const useLoginMutation = () => {
  return useMutation({
    mutationFn: login,
  });
};
