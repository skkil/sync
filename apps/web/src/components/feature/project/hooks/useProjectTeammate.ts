import { useQueryClient } from '@tanstack/react-query';
import { HTTPError } from 'ky';

import {
  getGetProjectByHandleQueryOptions,
  getGetProjectTeammatesQueryOptions,
  useRemoveTeammate as useRemoveTeammateMutation,
  useUpdateTeammate as useUpdateTeammateMutation,
} from '@/api/__generated__/project/project';
import type { UpdateTeammateRequest } from '@/api/__generated__/types';
import { server } from '@/lib/server';

const PROJECT_OWNER_CANNOT_BE_MODIFIED = 'PROJECT_OWNER_CANNOT_BE_MODIFIED';

interface ProjectTeammateErrorResponse {
  code?: string;
}

export class ProjectOwnerCannotBeModifiedError extends Error {}

async function rethrowProjectTeammateError(error: unknown): Promise<never> {
  if (error instanceof HTTPError && error.response.status === 403) {
    const body = (await error.response
      .clone()
      .json()
      .catch(() => null)) as ProjectTeammateErrorResponse | null;

    if (body?.code === PROJECT_OWNER_CANNOT_BE_MODIFIED) {
      throw new ProjectOwnerCannotBeModifiedError();
    }
  }

  throw error;
}

async function updateProjectTeammate({
  handle,
  teammateHandle,
  data,
}: {
  handle: string;
  teammateHandle: string;
  data?: UpdateTeammateRequest;
}) {
  try {
    const response = await server.patch(
      `projects/${handle}/teammates/${teammateHandle}`,
      { json: data },
    );

    return { data: undefined, status: 204 as const, headers: response.headers };
  } catch (error) {
    return rethrowProjectTeammateError(error);
  }
}

async function removeProjectTeammate({
  handle,
  teammateHandle,
}: {
  handle: string;
  teammateHandle: string;
}) {
  try {
    const response = await server.delete(
      `projects/${handle}/teammates/${teammateHandle}`,
    );

    return { data: undefined, status: 204 as const, headers: response.headers };
  } catch (error) {
    return rethrowProjectTeammateError(error);
  }
}

async function invalidateProjectTeammates(
  queryClient: ReturnType<typeof useQueryClient>,
  handle: string,
) {
  await Promise.all([
    queryClient.invalidateQueries(getGetProjectTeammatesQueryOptions(handle)),
    queryClient.invalidateQueries(getGetProjectByHandleQueryOptions(handle)),
  ]);
}

export function useUpdateProjectTeammate() {
  const queryClient = useQueryClient();

  return useUpdateTeammateMutation({
    mutation: {
      mutationFn: updateProjectTeammate,
      onSuccess: async (_data, { handle }) => {
        await invalidateProjectTeammates(queryClient, handle);
      },
    },
  });
}

export function useRemoveProjectTeammate() {
  const queryClient = useQueryClient();

  return useRemoveTeammateMutation({
    mutation: {
      mutationFn: removeProjectTeammate,
      onSuccess: async (_data, { handle }) => {
        await invalidateProjectTeammates(queryClient, handle);
      },
    },
  });
}
