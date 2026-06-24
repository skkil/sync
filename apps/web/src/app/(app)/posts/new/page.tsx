'use client';

import { redirect, useRouter } from 'next/navigation';

import { useCreatePost } from '@/api/__generated__/post/post';
import { CreatePostRequestType } from '@/api/__generated__/types/CreatePostRequestType';
import PostEditor from '@/components/feature/post/editor/PostEditor';
import { isAuthenticated, isOnboarded } from '@/lib/auth';
import { useSession } from '@/lib/auth/client';

export default function CreatePostPage() {
  const router = useRouter();
  const { data: session } = useSession();

  const { mutate: createPost } = useCreatePost({
    mutation: {
      onSuccess: ({ data }) => {
        router.push(`/posts/${data.slug}`);
      },
    },
  });

  if (isAuthenticated(session) && !isOnboarded(session)) {
    redirect('/onboarding');
  }

  if (!isAuthenticated(session)) {
    redirect('/auth/login');
  }

  return (
    <div className="h-full">
      <PostEditor
        onSubmit={({ title, tags, projectId, content }) => {
          createPost({
            data: {
              type: CreatePostRequestType.Long,
              title,
              projectId,
              tags,
              content: {
                json: content.json,
                text: content.text,
                mediaIds: content.media.map((media) => media.id),
              },
            },
          });
        }}
      />
    </div>
  );
}
