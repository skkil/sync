'use client';

import { useRouter, useSearchParams } from 'next/navigation';

import { useCreatePost } from '@/api/__generated__/post/post';
import { type CreatePostRequest } from '@/api/__generated__/types';
import PostEditor from '@/components/feature/post/editor/PostEditor';
import { PostStatus, PostType } from '@/components/feature/post/types/post';
import { useAuthGuard } from '@/hooks/use-auth-guard';
import ROUTES from '@/util/routes';

function getInitialPostType(value: string | null): PostType {
  if (value === PostType.SHORT || value === PostType.QUESTION) {
    return value;
  }

  return PostType.LONG;
}

export default function CreatePostPage() {
  const router = useRouter();
  const searchParams = useSearchParams();
  useAuthGuard();

  const { mutate: createPost, isPending: isCreatingPost } = useCreatePost({
    mutation: {
      onSuccess: ({ data }, variables) => {
        if (variables.data?.status === PostStatus.DRAFT) {
          router.replace(ROUTES.POST_EDIT(data.slug));
          return;
        }

        router.push(ROUTES.POST(data.slug));
      },
    },
  });

  return (
    <PostEditor
      type={getInitialPostType(searchParams.get('type'))}
      isSubmitting={isCreatingPost}
      onSubmit={({ title, type, status, tags, content }) => {
        createPost({
          data: {
            type,
            status,
            title,
            tags,
            content: {
              json: content.json,
              text: content.text,
              mediaIds: content.media.map((media) => media.id),
            },
          } satisfies CreatePostRequest,
        });
      }}
    />
  );
}
