'use client';

import { useQueryClient } from '@tanstack/react-query';
import { useTranslations } from 'next-intl';
import { useParams, useRouter } from 'next/navigation';
import { toast } from 'sonner';

import {
  useGetPostBySlug,
  useUpdateProjectPost,
} from '@/api/__generated__/post/post';
import PostEditor from '@/components/feature/post/editor/PostEditor';
import { invalidatePostQueries } from '@/components/feature/post/hooks/postQueryKeys';
import { PostStatus } from '@/components/feature/post/types/post';
import { toPostSummary } from '@/components/feature/post/viewer/types';
import { Skeleton } from '@/components/ui/skeleton';
import { useAuthGuard } from '@/hooks/use-auth-guard';
import ROUTES from '@/util/routes';

export default function EditProjectPostPage() {
  const { handle, slug } = useParams<{ handle: string; slug: string }>();
  const router = useRouter();
  const t = useTranslations('components.editor');
  const queryClient = useQueryClient();
  useAuthGuard();

  const { data, isPending, isError } = useGetPostBySlug(slug);
  const { mutate: updateProjectPost, isPending: isUpdating } =
    useUpdateProjectPost({
      mutation: {
        onSuccess: () => invalidatePostQueries(queryClient),
      },
    });

  const post = data?.data;

  if (isPending) {
    return <Skeleton className="h-96 w-full" />;
  }

  if (isError || !post) {
    return (
      <div className="mx-auto flex min-h-96 max-w-2xl items-center justify-center px-6 text-sm text-muted-foreground">
        {t('messages.edit-unavailable')}
      </div>
    );
  }

  const summary = toPostSummary(post.summary);
  const content = post.content;

  if (!summary.isAuthor) {
    return (
      <div className="mx-auto flex min-h-96 max-w-2xl items-center justify-center px-6 text-sm text-muted-foreground">
        {t('messages.edit-forbidden')}
      </div>
    );
  }

  const detailPath = summary.project?.handle
    ? ROUTES.PROJECT_POST(summary.project.handle, summary.slug)
    : ROUTES.POST(summary.slug);
  const editorProject =
    summary.project?.handle && summary.project.name
      ? { handle: summary.project.handle, name: summary.project.name }
      : undefined;

  return (
    <PostEditor
      key={summary.id}
      isEditing
      type={summary.type}
      routeProjectHandle={handle}
      summary={summary}
      content={content}
      isSubmitting={isUpdating}
      project={editorProject}
      onSubmit={({ title, type, status, tags, projectTags, content }) => {
        updateProjectPost(
          {
            handle,
            postId: `${summary.id}`,
            data: {
              title,
              type,
              status,
              tags,
              projectTags,
              content: {
                json: content.json,
                text: content.text,
                mediaIds: content.media.map((media) => media.id),
              },
            },
          },
          {
            onSuccess: () => {
              toast.success(t('messages.update-success'));
              if (status === PostStatus.PUBLISHED) {
                router.push(detailPath);
                router.refresh();
              }
            },
            onError: () => {
              toast.error(t('messages.update-error'));
            },
          },
        );
      }}
    />
  );
}
