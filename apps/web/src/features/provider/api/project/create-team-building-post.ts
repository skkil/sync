import { useMutation } from '@tanstack/react-query';

import { server } from '@/lib/server';

import {
  GetProjectTeamBuildingPostsQueryData,
  GetProjectTeamBuildingPostsQueryKey,
} from './get-project-team-building-posts';

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
    onMutate: (variables, context) => {
      const newPost = {
        id: `temp-${Date.now()}`,
        title: variables.title,
        content: variables.content || '',
      };

      context.client.setQueryData<GetProjectTeamBuildingPostsQueryData>(
        GetProjectTeamBuildingPostsQueryKey(projectId),
        (old) => [...(old || []), newPost],
      );

      return { newPost };
    },
    onSuccess: (data, _variables, onMutateResult, context) => {
      context.client.setQueryData<GetProjectTeamBuildingPostsQueryData>(
        GetProjectTeamBuildingPostsQueryKey(projectId),
        (old) =>
          old
            ? old.map((post) =>
                post.id === onMutateResult.newPost.id
                  ? { ...post, id: data.id }
                  : post,
              )
            : [],
      );
    },
    onError: (_error, _variables, onMutateResult, context) => {
      context.client.setQueryData<GetProjectTeamBuildingPostsQueryData>(
        GetProjectTeamBuildingPostsQueryKey(projectId),
        (old) =>
          old
            ? old.filter((post) => post.id !== onMutateResult?.newPost.id)
            : [],
      );
    },
  });
}
