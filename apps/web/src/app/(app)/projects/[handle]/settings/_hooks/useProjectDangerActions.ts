'use client';

import { useMutation, useQueryClient } from '@tanstack/react-query';

import {
  deleteProject,
  getGetMyProjectsQueryKey,
  getGetProjectByHandleQueryKey,
  getGetProjectTeammatesQueryKey,
  getSearchMyProjectsQueryKey,
  getSearchProjectsQueryKey,
  leaveProject,
} from '@/api/__generated__/project/project';

export function useDeleteProjectMutation(handle: string) {
  const queryClient = useQueryClient();

  return useMutation({
    mutationFn: () => deleteProject(handle),
    onSuccess: async () => {
      queryClient.removeQueries({
        queryKey: getGetProjectByHandleQueryKey(handle),
        exact: true,
      });
      queryClient.removeQueries({
        queryKey: getGetProjectTeammatesQueryKey(handle),
        exact: true,
      });

      await Promise.all([
        queryClient.invalidateQueries({
          queryKey: getGetMyProjectsQueryKey(),
        }),
        queryClient.invalidateQueries({
          queryKey: getSearchMyProjectsQueryKey(),
        }),
        queryClient.invalidateQueries({
          queryKey: getSearchProjectsQueryKey(),
        }),
      ]);
    },
  });
}

export function useLeaveProjectMutation(handle: string) {
  const queryClient = useQueryClient();

  return useMutation({
    mutationFn: () => leaveProject(handle),
    onSuccess: async () => {
      queryClient.removeQueries({
        queryKey: getGetProjectByHandleQueryKey(handle),
        exact: true,
      });
      queryClient.removeQueries({
        queryKey: getGetProjectTeammatesQueryKey(handle),
        exact: true,
      });

      await Promise.all([
        queryClient.invalidateQueries({
          queryKey: getGetMyProjectsQueryKey(),
        }),
        queryClient.invalidateQueries({
          queryKey: getSearchMyProjectsQueryKey(),
        }),
      ]);
    },
  });
}
