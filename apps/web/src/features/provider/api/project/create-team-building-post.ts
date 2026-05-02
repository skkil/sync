import { useMutation } from '@tanstack/react-query';

import { server } from '@/lib/server';

interface CreateTeamBuildingPostRequest {
  title: string;
  content?: string;
}

interface CreateTeamBuildingPostResponse {
  id: string;
}

function createTeamBuildingPost(
  projectId: string,
  data: CreateTeamBuildingPostRequest,
) {
  return server
    .post<CreateTeamBuildingPostResponse>(
      `projects/${projectId}/team-building`,
      {
        json: data,
      },
    )
    .json();
}

export default function useCreateTeamBuildingPostMutation(projectId: string) {
  return useMutation({
    mutationFn: (data: CreateTeamBuildingPostRequest) =>
      createTeamBuildingPost(projectId, data),
  });
}
