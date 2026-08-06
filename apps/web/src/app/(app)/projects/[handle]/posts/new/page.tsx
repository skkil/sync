'use client';

import { useTranslations } from 'next-intl';
import { useParams, useRouter, useSearchParams } from 'next/navigation';

import { useCreateProjectPost } from '@/api/__generated__/post/post';
import { useGetProjectByHandle } from '@/api/__generated__/project/project';
import { type CreateProjectPostRequest } from '@/api/__generated__/types';
import PostEditor from '@/components/feature/post/editor/PostEditor';
import { useApplySeriesSelection } from '@/components/feature/post/hooks/useApplySeriesSelection';
import { PostStatus, PostType } from '@/components/feature/post/types/post';
import { useAuthGuard } from '@/hooks/use-auth-guard';
import ROUTES from '@/util/routes';

function getInitialPostType(value: string | null): PostType {
  if (value === PostType.SHORT || value === PostType.QUESTION) {
    return value;
  }

  return PostType.LONG;
}

export default function CreateProjectPostPage() {
  const router = useRouter();
  const searchParams = useSearchParams();
  const { handle } = useParams<{ handle: string }>();
  const t = useTranslations('components.editor.scope');
  useAuthGuard();
  const { data: projectData } = useGetProjectByHandle(handle);

  const applySeries = useApplySeriesSelection();
  const { mutate: createProjectPost, isPending: isCreatingPost } =
    useCreateProjectPost();

  return (
    <PostEditor
      type={getInitialPostType(searchParams.get('type'))}
      isSubmitting={isCreatingPost || !projectData}
      project={{
        handle,
        name: projectData?.data.summary.name ?? t('project-loading'),
      }}
      onSubmit={({
        title,
        type,
        status,
        tags,
        projectTags,
        series,
        coverMediaId,
        content,
      }) => {
        createProjectPost(
          {
            handle,
            data: {
              type,
              status,
              title,
              tags,
              projectTags,
              coverMediaId,
              content: {
                json: content.json,
                text: content.text,
                mediaIds: content.media.map((media) => media.id),
              },
            } satisfies CreateProjectPostRequest,
          },
          {
            onSuccess: async ({ data }, variables) => {
              if (series) {
                await applySeries({
                  slug: data.slug,
                  projectHandle: handle,
                  selection: series,
                  initial: null,
                });
              }

              if (variables.data?.status === PostStatus.DRAFT) {
                router.replace(ROUTES.PROJECT_POST_EDIT(handle, data.slug));
                return;
              }

              router.push(ROUTES.PROJECT_POST(handle, data.slug));
            },
          },
        );
      }}
    />
  );
}
