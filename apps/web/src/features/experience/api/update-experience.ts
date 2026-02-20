import { useMutation } from '@tanstack/react-query';

import { server } from '@/lib/server';
import {
  Experience,
  ExperienceType,
  ExperienceVisibility,
} from '@/types/experience';

type UpdateExperienceRequest = {
  visibility?: ExperienceVisibility;
} & (
  | {
      type: ExperienceType.EMPLOYMENT;
    }
  | {
      type: ExperienceType.EDUCATION;
      major?: string;
      gpa?: number;
    }
);

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
      const { experienceId, data } = variables;

      const oldExperience = context.client
        .getQueryData<Experience[]>(['users', userId, 'experiences'])
        ?.find((e) => e.id === experienceId);

      context.client.setQueryData(
        ['users', userId, 'experiences'],
        (old: Experience[]) => {
          return [
            ...old.filter((e) => e.id !== experienceId),
            {
              ...old.find((e) => e.id === experienceId),
              ...data,
            },
          ];
        },
      );

      return { oldExperience };
    },
    onError: (_error, variables, onMutateResult, context) => {
      const { experienceId } = variables;

      context.client.setQueryData(
        ['users', userId, 'experiences'],
        (old: Experience[]) => {
          return [
            ...old.filter((e) => e.id !== experienceId),
            onMutateResult?.oldExperience,
          ];
        },
      );
    },
  });
}
