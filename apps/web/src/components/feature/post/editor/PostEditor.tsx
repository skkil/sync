'use client';

import { CharacterCount, Placeholder } from '@tiptap/extensions';
import { EditorContent, useEditor } from '@tiptap/react';
import StarterKit from '@tiptap/starter-kit';
import { useLocale, useTranslations } from 'next-intl';
import { useRouter } from 'next/navigation';
import { useState } from 'react';

import { useCreatePost } from '@/api/__generated__/post/post';
import { Button } from '@/components/ui/button';
import { ScrollArea } from '@/components/ui/scroll-area';
import { Separator } from '@/components/ui/separator';

import { EditorBubbleMenu } from './components/EditorBubbleMenu';
import { EditorTemplates } from './components/EditorTemplates';
import { ProjectInput } from './components/ProjectInput';
import { TagInput } from './components/TagInput';
import { CommandsExtension } from './extensions/commands';
import { ImageNode } from './extensions/nodes/image';

export default function Editor() {
  const t = useTranslations('components.editor');
  const locale = useLocale();
  const router = useRouter();

  const { mutate: createPost } = useCreatePost();

  const [title, setTitle] = useState('');
  const [tags, setTags] = useState<string[]>([]);
  const [projectId, setProjectId] = useState<number | null>(null);
  const [isEditorEmpty, setIsEditorEmpty] = useState(true);

  const editor = useEditor({
    extensions: [
      StarterKit,
      Placeholder.configure({
        placeholder: t('placeholders.content'),
      }),
      CharacterCount,
      CommandsExtension,
      ImageNode,
    ],
    content: '',
    immediatelyRender: false,
    onUpdate: ({ editor }) => {
      setIsEditorEmpty(editor.isEmpty);
    },
  });

  const handleSubmit = () => {
    if (!editor) {
      return;
    }

    createPost(
      {
        data: {
          title,
          type: 'LONG',
          tags,
          projectId,
          content: {
            json: JSON.stringify(editor.getJSON()),
            text: editor.getText(),
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

  return (
    <div className="flex flex-col h-full">
      <div className="flex-1 overflow-y-auto py-12 flex flex-col gap-4">
        <input
          className="w-full shrink-0 resize-none bg-transparent text-4xl font-bold outline-none placeholder:text-muted-foreground leading-tight"
          placeholder={t('placeholders.title')}
          value={title}
          onChange={(e) => setTitle(e.target.value)}
        />

        <ScrollArea className="flex-1 min-h-0 max-h-screen">
          <EditorContent editor={editor} />
          {editor && <EditorBubbleMenu editor={editor} />}
          {isEditorEmpty && (
            <EditorTemplates
              locale={locale}
              onSelect={(template) => {
                setTitle(template.title);
                editor?.commands.setContent(template.content);
              }}
            />
          )}
        </ScrollArea>
      </div>

      <Separator />

      <div className="px-6 py-3 flex flex-col gap-3 shrink-0">
        <ProjectInput
          projectId={projectId}
          onChange={(id) => {
            setProjectId(id);
          }}
        />
        <TagInput tags={tags} onChange={setTags} />
        <div className="flex justify-end">
          <Button onClick={handleSubmit}>{t('submit')}</Button>
        </div>
      </div>
    </div>
  );
}
