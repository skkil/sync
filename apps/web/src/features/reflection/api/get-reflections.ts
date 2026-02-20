import { useQuery } from '@tanstack/react-query';

import { server } from '@/lib/server';
import { Reflection } from '@/types/experience';

interface GetReflectionsResponse {
  reflections: {
    id: string;
    content: string;
    createdAt: string;
  }[];
}

async function getReflections(experienceId: string): Promise<Reflection[]> {
  return server
    .get<GetReflectionsResponse>(`experiences/${experienceId}/reflections`)
    .json()
    .then((res) => res.reflections)
    .then((reflections) =>
      reflections.map((reflection) => ({
        ...reflection,
        createdAt: new Date(reflection.createdAt),
      })),
    );
}

export const useGetReflectionsQuery = (experienceId: string) => {
  return useQuery({
    queryKey: ['experience', experienceId, 'reflections'],
    queryFn: () => getReflections(experienceId),
  });
};
