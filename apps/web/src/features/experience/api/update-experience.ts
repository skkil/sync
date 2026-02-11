import { useMutation } from '@tanstack/react-query';

import { server } from '@/lib/server';
import { Experience, ExperienceVisibility } from '@/types/experience';

interface UpdateExperienceRequest {
  visibility?: ExperienceVisibility;
}

async function updateExperience({
  experienceId,
  data,
}: {
  experienceId: string;
  data: UpdateExperienceRequest;
}) {
  return server.patch<void>(`experiences/${experienceId}`, {
    json: data,
  });
}

export function useUpdateExperienceMutation(userId: string) {
  return useMutation({
    mutationFn: updateExperience,
    onMutate: async (variables, context) => {
      const {
        experienceId,
        data: { visibility },
      } = variables;

      context.client.setQueryData(
        ['users', userId, 'experiences'],
        (old: Experience[]) => {
          return [
            ...old.filter((e) => e.id !== experienceId),
            {
              ...old.find((e) => e.id === experienceId),
              visibility,
            },
          ];
        },
      );
    },
  });
}
