import {
  getGetProjectsByUserQueryKey,
  getSearchMyProjectsQueryKey,
  useCreateProject as useCreateProjectMutation,
} from '@/api/__generated__/project/project';
import { useSession } from '@/lib/auth/client';

export function useCreateProject() {
  const { data: session } = useSession();

  return useCreateProjectMutation({
    mutation: {
      onSuccess: async (_data, _variables, _onMutateResult, context) => {
        await context.client.invalidateQueries({
          queryKey: getSearchMyProjectsQueryKey(),
        });

        if (session?.user.handle) {
          await context.client.invalidateQueries({
            queryKey: getGetProjectsByUserQueryKey(session.user.handle),
          });
        }
      },
    },
  });
}
