import { useQueryClient } from '@tanstack/react-query';

import {
  getGetMyProjectJoinRequestsQueryOptions,
  getGetMyProjectsQueryKey,
  getGetProjectByHandleQueryOptions,
  getGetProjectJoinRequestsQueryOptions,
  getGetProjectTeammatesQueryKey,
  getSearchMyProjectsQueryKey,
  useApproveProjectJoinRequest as useApproveProjectJoinRequestMutation,
  useCancelProjectJoinRequest as useCancelProjectJoinRequestMutation,
  useDeclineProjectJoinRequest as useDeclineProjectJoinRequestMutation,
  useJoinProject as useJoinProjectMutation,
} from '@/api/__generated__/project/project';

export function useJoinProject() {
  const queryClient = useQueryClient();

  return useJoinProjectMutation({
    mutation: {
      onSuccess: async (_data, { handle }) => {
        await Promise.all([
          queryClient.invalidateQueries(
            getGetProjectByHandleQueryOptions(handle),
          ),
          queryClient.invalidateQueries(
            getGetMyProjectJoinRequestsQueryOptions(),
          ),
          // OPEN projects add the caller as a teammate immediately, so refresh
          // the "my projects" lists that drive the sidebar and project switcher.
          queryClient.invalidateQueries({
            queryKey: getSearchMyProjectsQueryKey(),
          }),
          queryClient.invalidateQueries({
            queryKey: getGetMyProjectsQueryKey(),
          }),
        ]);
      },
    },
  });
}

export function useCancelJoinRequest() {
  const queryClient = useQueryClient();

  return useCancelProjectJoinRequestMutation({
    mutation: {
      onSuccess: async () => {
        await queryClient.invalidateQueries(
          getGetMyProjectJoinRequestsQueryOptions(),
        );
      },
    },
  });
}

export function useApproveJoinRequest() {
  const queryClient = useQueryClient();

  return useApproveProjectJoinRequestMutation({
    mutation: {
      onSuccess: async (_data, { handle }) => {
        await Promise.all([
          queryClient.invalidateQueries(
            getGetProjectJoinRequestsQueryOptions(handle),
          ),
          queryClient.invalidateQueries({
            queryKey: getGetProjectTeammatesQueryKey(handle),
          }),
          queryClient.invalidateQueries({
            queryKey: getGetMyProjectsQueryKey(),
          }),
        ]);
      },
    },
  });
}

export function useDeclineJoinRequest() {
  const queryClient = useQueryClient();

  return useDeclineProjectJoinRequestMutation({
    mutation: {
      onSuccess: async (_data, { handle }) => {
        await queryClient.invalidateQueries(
          getGetProjectJoinRequestsQueryOptions(handle),
        );
      },
    },
  });
}
