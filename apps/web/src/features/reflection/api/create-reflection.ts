import { useMutation } from '@tanstack/react-query';

import { server } from '@/lib/server';
import { Reflection } from '@/types/experience';

interface CreateReflectionRequest {
  content: string;
}

async function createReflection(
  experienceId: string,
  request: CreateReflectionRequest,
) {
  return server.post(`experiences/${experienceId}/reflections`, {
    json: request,
  });
}

export const useCreateReflectionMutation = (experienceId: string) => {
  return useMutation({
    mutationFn: (request: CreateReflectionRequest) =>
      createReflection(experienceId, request),
    onMutate: async (variables, context) => {
      const reflections: Reflection[] | undefined = context.client.getQueryData(
        ['experience', experienceId, 'reflections'],
      );

      const newReflection: Reflection = {
        id: `temp-${Date.now()}`,
        content: variables.content,
        createdAt: new Date(),
      };

      context.client.setQueryData(
        ['experience', experienceId, 'reflections'],
        [newReflection, ...(reflections || [])],
      );

      return { newReflection };
    },
    onError: (_error, _variables, onMutateResult, context) => {
      const reflections: Reflection[] | undefined = context.client.getQueryData(
        ['experience', experienceId, 'reflections'],
      );

      context.client.setQueryData(
        ['experience', experienceId, 'reflections'],
        reflections?.filter((r) => r.id !== onMutateResult?.newReflection.id) ||
          [],
      );
    },
  });
};
