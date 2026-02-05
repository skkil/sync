import { useMutation } from '@tanstack/react-query';

import { server } from '@/lib/server';
import { ExperienceType } from '@/types/experience';

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

export function useCreateExperienceMutation() {
  return useMutation({
    mutationFn: createExperience,
  });
}
