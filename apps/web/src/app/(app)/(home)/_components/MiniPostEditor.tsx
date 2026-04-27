'use client';

import { Placeholder } from '@tiptap/extensions';
import { EditorContent, useEditor } from '@tiptap/react';
import StarterKit from '@tiptap/starter-kit';
import { useTranslations } from 'next-intl';
import { useRouter } from 'next/navigation';
import { useState } from 'react';

import { useCreateReflection } from '@/api/__generated__/reflection/reflection';
import { Avatar, AvatarFallback, AvatarImage } from '@/components/ui/avatar';
import { Button } from '@/components/ui/button';
import { CardContent } from '@/components/ui/card';
import { ScrollArea } from '@/components/ui/scroll-area';
import { Skeleton } from '@/components/ui/skeleton';
import { useSession } from '@/lib/auth/client';
import { cn } from '@/lib/utils';

export default function MiniPostEditor() {
  const t = useTranslations('pages.home.post-editor');
  const router = useRouter();

  const { data: session, isPending: isSessionPending } = useSession();

  const { mutate: createPost } = useCreateReflection();

  const [isFocused, setIsFocused] = useState(false);
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

    createPost(
      {
        data: {
          content: JSON.stringify(editor.getJSON()),
        },
      },
      {
        onSuccess: ({ data: { slug } }) => {
          router.push(`/posts/${slug}`);
        },
      },
    );
  };

  return (
    <div className="w-full max-w-2xl mx-auto">
      <CardContent className="p-4">
        <div className="flex gap-3">
          <Avatar>
            <AvatarImage src={session.user.image ?? undefined} />
            <AvatarFallback>{session.user.name[0]}</AvatarFallback>
          </Avatar>

          <div className="flex-1 space-y-3">
            <div
              className={cn(
                'rounded-lg border transition-all',
                isFocused ? 'border-primary h-64' : 'border-border h-32',
              )}
              onClick={() => editor?.commands.focus()}
            >
              <ScrollArea className="h-full w-full">
                <EditorContent
                  editor={editor}
                  onFocus={() => setIsFocused(true)}
                  onBlur={() => setIsFocused(false)}
                  className="w-full max-w-full px-3 py-2"
                />
              </ScrollArea>
            </div>

            <div className="flex items-center justify-between">
              <div className="flex items-center gap-2"></div>

              <div className="flex items-center gap-3">
                <Button disabled={isEmpty} onClick={handleSubmit}>
                  <span>{t('post.label')}</span>
                </Button>
              </div>
            </div>
          </div>
        </div>
      </CardContent>
    </div>
  );
}
