import {
  getGetProjectsByUserQueryKey,
  useCreateProject as useCreateProjectMutation,
} from '@/api/__generated__/project/project';
import { useSession } from '@/lib/auth/client';

export function useCreateProject() {
  const { data: session } = useSession();

  return useCreateProjectMutation({
    mutation: {
      onSuccess: async (_data, _variables, _onMutateResult, context) => {
        if (session?.user.handle) {
          await context.client.invalidateQueries({
            queryKey: getGetProjectsByUserQueryKey(session.user.handle),
          });
        }
      },
    },
  });
}
