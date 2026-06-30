import { useMutation } from '@tanstack/react-query';

import { server } from '@/lib/server';
import { Experience, ExperienceType } from '@/types/experience';

type CreateExperienceRequest = {
  providerId: string;
  startDate: Date;
  endDate?: Date;
} & (
  | {
      type: ExperienceType.EDUCATION;
      major: string;
      gpa: number;
    }
  | {
      type: ExperienceType.EMPLOYMENT;
    }
);

interface CreateExperienceResponse {
  id: string;
}

async function createExperience(
  data: CreateExperienceRequest,
): Promise<CreateExperienceResponse> {
  return server
    .post<CreateExperienceResponse>('experiences', {
      json: data,
    })
    .json();
}

export function useCreateExperienceMutation(userId: string) {
  return useMutation({
    mutationFn: createExperience,
    onMutate: async (data, context) => {
      const experiences = context.client.getQueryData<Experience[]>([
        'users',
        userId,
        'experiences',
      ]);

      const tempId = 'temp-id' + Date.now();

      context.client.setQueryData<Experience[]>(
        ['users', userId, 'experiences'],
        [
          ...(experiences || []),
          {
            id: tempId,
            visibility: 'PRIVATE',
            provider: {
              id: data.providerId,
              name: '',
            },
            ...data,
            startDate: new Date(data.startDate),
            endDate: data.endDate ? new Date(data.endDate) : null,
          },
        ],
      );

      return {
        id: tempId,
      };
    },
    onSuccess: (data, _variables, onMutateResult, context) => {
      const id = data.id;

      const experiences = context.client.getQueryData<Experience[]>([
        'users',
        userId,
        'experiences',
      ]);

      context.client.setQueryData<Experience[]>(
        ['users', userId, 'experiences'],
        experiences?.map((experience) =>
          experience.id === onMutateResult.id
            ? { ...experience, id }
            : experience,
        ) || [],
      );
    },
    onError: (_error, _variables, onMutateResult, context) => {
      const experiences = context.client.getQueryData<Experience[]>([
        'users',
        userId,
        'experiences',
      ]);
      context.client.setQueryData<Experience[]>(
        ['users', userId, 'experiences'],
        experiences?.filter(
          (experience) => experience.id !== onMutateResult?.id,
        ) || [],
      );
    },
  });
}
