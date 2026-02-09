import { useMutation } from '@tanstack/react-query';

import { server } from '@/lib/server';
import { ExperienceVisibility } from '@/types/experience';

interface UpdateExperienceRequest {
  visibility: ExperienceVisibility;
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

export function useUpdateExperienceMutation() {
  return useMutation({
    mutationFn: updateExperience,
  });
}
