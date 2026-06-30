'use client';

import { Placeholder } from '@tiptap/extensions';
import { EditorContent, useEditor } from '@tiptap/react';
import StarterKit from '@tiptap/starter-kit';
import { useTranslations } from 'next-intl';
import Link from 'next/link';
import { useRouter } from 'next/navigation';
import { useState } from 'react';

import { useCreatePost } from '@/api/__generated__/post/post';
import { Avatar, AvatarFallback, AvatarImage } from '@/components/ui/avatar';
import { Button } from '@/components/ui/button';
import { ScrollArea } from '@/components/ui/scroll-area';
import { Skeleton } from '@/components/ui/skeleton';
import { Tabs, TabsList, TabsTrigger } from '@/components/ui/tabs';
import { PostType } from '@/features/post/constants/post-type';
import { useSession } from '@/lib/auth/client';

export default function MiniPostEditor() {
  const t = useTranslations('pages.home.post-editor');
  const tType = useTranslations('components.post.type');
  const router = useRouter();

  const { data: session, isPending: isSessionPending } = useSession();

  const { mutate: createPost, isPending: isCreatingPost } = useCreatePost();

  const [postType, setPostType] = useState<PostType>(PostType.Short);
  const [isEmpty, setIsEmpty] = useState(true);

  const editor = useEditor({
    extensions: [
      StarterKit,
      Placeholder.configure({
        placeholder: t('placeholder'),
        showOnlyWhenEditable: true,
      }),
    ],
    content: '',
    onUpdate: ({ editor }) => {
      if (!editor) {
        setIsEmpty(true);
        return;
      }

      setIsEmpty(editor.isEmpty);
    },
    immediatelyRender: false,
    editorProps: {
      attributes: {
        class: 'focus:outline-none focus:ring-0',
      },
    },
  });

  if (isSessionPending) {
    return <Skeleton className="h-24" />;
  }

  if (!session) {
    return null;
  }

  const handleSubmit = () => {
    if (!editor || editor.isEmpty) {
      return;
    }

    if (postType !== PostType.Short) {
      router.push(`/posts/new?type=${postType}`);
      return;
    }

    createPost(
      {
        data: {
          type: postType,
          content: {
            text: editor.getText(),
            json: JSON.stringify(editor.getJSON()),
          },
        },
      },
      {
        onSuccess: ({ data: { slug } }) => {
          router.push(`/posts/${slug}`);
        },
      },
    );
  };

  const isPostDisabled = isEmpty || isCreatingPost;

  return (
    <div className="w-full mx-auto">
      <div className="flex gap-3">
        <Avatar>
          <AvatarImage src={session.user.image ?? undefined} />
          <AvatarFallback>{session.user.name[0]}</AvatarFallback>
        </Avatar>

        <div className="flex-1 space-y-3">
          <Tabs
            value={postType}
            onValueChange={(value) => setPostType(value as PostType)}
          >
            <TabsList>
              {Object.values(PostType).map((type) => (
                <TabsTrigger key={type} value={type}>
                  {tType(type)}
                </TabsTrigger>
              ))}
            </TabsList>
          </Tabs>

          <div
            className="h-32 rounded-lg border border-border transition-colors focus-within:border-primary"
            onClick={() => editor?.commands.focus()}
          >
            <ScrollArea className="h-full w-full">
              <div className="space-y-3 px-3 py-2">
                <EditorContent editor={editor} className="w-full max-w-full" />
              </div>
            </ScrollArea>
          </div>

          <div className="flex items-center justify-end gap-3">
            <Button asChild>
              <Link href="/posts/new">{t('post.advanced')}</Link>
            </Button>

            <Button
              disabled={isPostDisabled}
              isPending={isCreatingPost}
              onClick={handleSubmit}
            >
              <span>
                {postType === PostType.Short
                  ? t('post.label')
                  : t('post.advanced')}
              </span>
            </Button>
          </div>
        </div>
      </div>
    </div>
  );
}
