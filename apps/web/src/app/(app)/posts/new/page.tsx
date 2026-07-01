'use client';

import { redirect, useRouter, useSearchParams } from 'next/navigation';

import { useCreatePost } from '@/api/__generated__/post/post';
import PostEditor from '@/components/feature/post/editor/PostEditor';
import { PostType } from '@/features/post/constants/post-type';
import { isAuthenticated, isOnboarded } from '@/lib/auth';
import { useSession } from '@/lib/auth/client';

function getInitialPostType(value: string | null): PostType {
  if (value === PostType.Short || value === PostType.Question) {
    return value;
  }

  return PostType.Long;
}

export default function CreatePostPage() {
  const router = useRouter();
  const searchParams = useSearchParams();
  const { data: session, isPending } = useSession();

  const { mutate: createPost } = useCreatePost({
    mutation: {
      onSuccess: ({ data }) => {
        router.push(`/posts/${data.slug}`);
      },
    },
  });

  if (!isPending) {
    if (isAuthenticated(session) && !isOnboarded(session)) {
      redirect('/onboarding');
    }

    if (!isAuthenticated(session)) {
      redirect('/auth/login');
    }
  }

  return (
    <div className="h-full">
      <PostEditor
        type={getInitialPostType(searchParams.get('type'))}
        onSubmit={({ title, type, tags, content }) => {
          createPost({
            data: {
              type,
              title,
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
