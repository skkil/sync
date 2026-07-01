'use client';

import {
  redirect,
  useParams,
  useRouter,
  useSearchParams,
} from 'next/navigation';

import { useCreatePost } from '@/api/__generated__/post/post';
import { useGetProjectByHandle } from '@/api/__generated__/project/project';
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

export default function CreateProjectPostPage() {
  const router = useRouter();
  const searchParams = useSearchParams();
  const { handle } = useParams<{ handle: string }>();
  const { data: session, isPending } = useSession();
  const { data: projectData } = useGetProjectByHandle(handle);

  const { mutate: createPost } = useCreatePost({
    mutation: {
      onSuccess: ({ data }) => {
        router.push(`/projects/${handle}/posts/${data.slug}`);
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
        project={
          projectData ? { handle, name: projectData.data.name } : undefined
        }
        onSubmit={({ title, type, tags, project, content }) => {
          createPost({
            data: {
              type,
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
    </div>
  );
}
