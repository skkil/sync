import { useQuery } from '@tanstack/react-query';

import { server } from '@/lib/server';
import {
  Experience,
  ExperienceType,
  ExperienceVisibility,
} from '@/types/experience';
import { url } from '@/util/server';

type GetExperiencesResponse = {
  experiences: ({
    id: string;
    visibility: ExperienceVisibility;
    provider: {
      id: string;
      name: string;
    };
    startDate: string;
    endDate: string | null;
  } & (
    | {
        type: ExperienceType.EMPLOYMENT;
      }
    | {
        type: ExperienceType.EDUCATION;
        gpa?: number;
        major?: string;
      }
  ))[];
};

async function getExperiences(userId: string): Promise<Experience[]> {
  return server
    .get<GetExperiencesResponse>(url(`profiles/${userId}/experiences`))
    .json()
    .then((data) =>
      data.experiences.map((experience) => ({
        ...experience,
        startDate: new Date(experience.startDate),
        endDate: experience.endDate ? new Date(experience.endDate) : null,
      })),
    );
}

export function useGetExperiencesQuery(userId: string) {
  return useQuery({
    queryKey: ['users', userId, 'experiences'],
    queryFn: () => getExperiences(userId),
  });
}
