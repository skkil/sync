import { useMutation } from '@tanstack/react-query';

import { server } from '@/lib/server';
import { Experience } from '@/types/experience';

async function deleteExperience(experienceId: string) {
  return server.delete(`experiences/${experienceId}`);
}

export function useDeleteExperienceMutation({ userId }: { userId: string }) {
  return useMutation({
    mutationFn: deleteExperience,
    onMutate: async (experienceId, context) => {
      const experiences = context.client.getQueryData<Experience[]>([
        'users',
        userId,
        'experiences',
      ]);

      const deletedExperience = experiences?.find(
        (exp) => exp.id === experienceId,
      );

      context.client.setQueryData<Experience[]>(
        ['users', userId, 'experiences'],
        experiences?.filter((exp) => exp.id !== experienceId) || [],
      );

      return { deletedExperience };
    },
    onError: (_error, _experienceId, onMutateResult, context) => {
      const experiences = context.client.getQueryData<Experience[]>([
        'users',
        userId,
        'experiences',
      ]);

      if (onMutateResult?.deletedExperience) {
        context.client.setQueryData<Experience[]>(
          ['users', userId, 'experiences'],
          [...(experiences || []), onMutateResult.deletedExperience],
        );
      }
    },
  });
}
