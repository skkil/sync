import { useMutation } from '@tanstack/react-query';

import { server } from '@/lib/server';

interface RegisterRequest {
  email: string;
  password: string;
}

function register(request: RegisterRequest) {
  return server.post<void>('auth/register', {
    json: request,
  });
}

export const useRegisterMutation = () => {
  return useMutation({
    mutationFn: register,
  });
};
