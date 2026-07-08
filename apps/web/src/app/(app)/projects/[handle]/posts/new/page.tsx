'use client';

import { useTranslations } from 'next-intl';
import { useParams, useRouter, useSearchParams } from 'next/navigation';

import { useCreatePost } from '@/api/__generated__/post/post';
import { useGetProjectByHandle } from '@/api/__generated__/project/project';
import PostEditor from '@/components/feature/post/editor/PostEditor';
import { PostType } from '@/components/feature/post/types/post';
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

  const { mutate: createPost, isPending: isCreatingPost } = useCreatePost({
    mutation: {
      onSuccess: ({ data }) => {
        router.push(ROUTES.PROJECT_POST(handle, data.slug));
      },
    },
  });

  return (
    <PostEditor
      type={getInitialPostType(searchParams.get('type'))}
      isSubmitting={isCreatingPost || !projectData}
      project={{
        handle,
        name: projectData?.data.summary.name ?? t('workspace-loading'),
      }}
      onSubmit={({ title, type, status, tags, project, content }) => {
        createPost({
          data: {
            type,
            status,
            title,
            tags,
            project,
            content: {
              json: content.json,
              text: content.text,
              mediaIds: content.media.map((media) => media.id),
            },
          },
        });
      }}
    />
  );
}
