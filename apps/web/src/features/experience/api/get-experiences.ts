import { useQuery } from '@tanstack/react-query';

import { server } from '@/lib/server';
import {
  Experience,
  ExperienceType,
  ExperienceVisibility,
} from '@/types/experience';
import { ProviderType } from '@/types/provider';
import { url } from '@/util/server';

type GetExperiencesResponse = {
  experiences: {
    id: string;
    type: ExperienceType;
    visibility: ExperienceVisibility;
    provider: {
      id: string;
      name: string;
    };
    startDate: string;
    endDate: string;
  }[];
};

async function getExperiences(
  userId: string,
  type?: ExperienceType,
): Promise<Experience[]> {
  return server
    .get<GetExperiencesResponse>(
      url(`profiles/${userId}/experiences`, {
        type,
      }),
    )
    .json()
    .then((data) =>
      data.experiences.map((experience) => ({
        id: experience.id,
        visibility: experience.visibility,
        provider: {
          id: experience.provider.id,
          name: experience.provider.name,
          type: ProviderType.SCHOOL,
        },
        startDate: new Date(experience.startDate),
        endDate: new Date(experience.endDate),
        type: experience.type,
      })),
    );
}

export function useGetExperiencesQuery(userId: string, type?: ExperienceType) {
  return useQuery({
    queryKey: ['users', userId, 'experiences', type],
    queryFn: () => getExperiences(userId, type),
  });
}
