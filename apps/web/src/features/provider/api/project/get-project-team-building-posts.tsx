import { useQuery } from '@tanstack/react-query';

import { server } from '@/lib/server';

interface GetProjectTeamBuildingPostsResponse {
  posts: {
    id: string;
    title: string;
    content: string;
  }[];
}

async function getProjectTeamBuildingPosts(projectId: string) {
  return server
    .get<GetProjectTeamBuildingPostsResponse>(
      `projects/${projectId}/team-building`,
    )
    .json()
    .then((data) => data.posts);
}

export function GetProjectTeamBuildingPostsQueryKey(projectId: string) {
  return ['project', projectId, 'team-building-posts'] as const;
}

export type GetProjectTeamBuildingPostsQueryData = Awaited<
  ReturnType<typeof getProjectTeamBuildingPosts>
>;

export default function useGetProjectTeamBuildingPostsQuery(projectId: string) {
  return useQuery<GetProjectTeamBuildingPostsQueryData>({
    queryKey: GetProjectTeamBuildingPostsQueryKey(projectId),
    queryFn: () => getProjectTeamBuildingPosts(projectId),
  });
}
