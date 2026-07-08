import { useQueryClient } from '@tanstack/react-query';

import {
  getGetProjectInvitationsQueryOptions,
  useCancelProjectInvitation as useCancelProjectInvitationMutation,
  useCreateProjectInvitation as useCreateProjectInvitationMutation,
} from '@/api/__generated__/project/project';

export function useCreateProjectInvitation() {
  const queryClient = useQueryClient();

  return useCreateProjectInvitationMutation({
    mutation: {
      onSuccess: async (_data, { handle }) => {
        await queryClient.invalidateQueries(
          getGetProjectInvitationsQueryOptions(handle),
        );
      },
    },
  });
}

export function useCancelProjectInvitation() {
  const queryClient = useQueryClient();

  return useCancelProjectInvitationMutation({
    mutation: {
      onSuccess: async (_data, { handle }) => {
        await queryClient.invalidateQueries(
          getGetProjectInvitationsQueryOptions(handle),
        );
      },
    },
  });
}
