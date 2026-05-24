'use client';

import {
  CodeIcon,
  TextBolderIcon,
  TextHOneIcon,
  TextHTwoIcon,
  TextItalicIcon,
  TextStrikethroughIcon,
} from '@phosphor-icons/react';
import { CharacterCount, Placeholder } from '@tiptap/extensions';
import { EditorContent, useEditor } from '@tiptap/react';
import { BubbleMenu } from '@tiptap/react/menus';
import StarterKit from '@tiptap/starter-kit';
import { useTranslations } from 'next-intl';
import { useRouter } from 'next/navigation';
import { useState } from 'react';

import { useCreateReflection } from '@/api/__generated__/reflection/reflection';
import { Button } from '@/components/ui/button';
import { CommandsExtension } from '@/components/ui/editor/extensions/commands';
import { ScrollArea } from '@/components/ui/scroll-area';
import { Separator } from '@/components/ui/separator';
import { cn } from '@/lib/utils';

export default function Editor() {
  const t = useTranslations('components.editor');
  const router = useRouter();

  const { mutate: createPost } = useCreateReflection();

  const [title, setTitle] = useState('');

  const editor = useEditor({
    extensions: [
      StarterKit,
      Placeholder.configure({
        placeholder: t('placeholders.content'),
      }),
      CharacterCount,
      CommandsExtension,
    ],
    content: '',
    immediatelyRender: false,
  });

  const handleSubmit = () => {
    if (!editor) {
      return;
    }

    createPost(
      {
        data: {
          title,
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
          onChange={(e) => {
            setTitle(e.target.value);
          }}
        />

        <ScrollArea className="flex-1 min-h-0 max-h-screen">
          <EditorContent editor={editor} />
          {editor && <EditorBubbleMenu editor={editor} />}
        </ScrollArea>
      </div>

      <Separator />

      <div className="px-6 py-3 flex items-center gap-4 shrink-0">
        <div className="flex justify-end w-full">
          <Button onClick={handleSubmit}>{t('submit')}</Button>
        </div>
      </div>
    </div>
  );
}

function EditorBubbleMenu({
  editor,
}: {
  editor: ReturnType<typeof useEditor>;
}) {
  const buttons = [
    {
      id: 'bold',
      icon: <TextBolderIcon />,
      isActive: () => editor?.isActive('bold') ?? false,
      onClick: () => editor?.chain().focus().toggleBold().run(),
    },
    {
      id: 'italic',
      icon: <TextItalicIcon />,
      isActive: () => editor?.isActive('italic') ?? false,
      onClick: () => editor?.chain().focus().toggleItalic().run(),
    },
    {
      id: 'strike',
      icon: <TextStrikethroughIcon />,
      isActive: () => editor?.isActive('strike') ?? false,
      onClick: () => editor?.chain().focus().toggleStrike().run(),
    },
    {
      id: 'code',
      icon: <CodeIcon />,
      isActive: () => editor?.isActive('code') ?? false,
      onClick: () => editor?.chain().focus().toggleCode().run(),
    },
    null,
    {
      id: 'h1',
      icon: <TextHOneIcon />,
      isActive: () => editor?.isActive('heading', { level: 1 }) ?? false,
      onClick: () => editor?.chain().focus().toggleHeading({ level: 1 }).run(),
    },
    {
      id: 'h2',
      icon: <TextHTwoIcon />,
      isActive: () => editor?.isActive('heading', { level: 2 }) ?? false,
      onClick: () => editor?.chain().focus().toggleHeading({ level: 2 }).run(),
    },
  ];

  return (
    <BubbleMenu
      editor={editor}
      className="flex items-center gap-0.5 rounded-lg border bg-popover p-1 shadow-lg"
    >
      {buttons.map((btn, i) =>
        btn === null ? (
          <Separator key={i} orientation="vertical" className="mx-1 h-5" />
        ) : (
          <button
            key={btn.id}
            type="button"
            onClick={btn.onClick}
            className={cn(
              'rounded p-1.5 transition-colors hover:bg-accent',
              btn.isActive() && 'bg-accent text-accent-foreground',
            )}
          >
            {btn.icon}
          </button>
        ),
      )}
    </BubbleMenu>
  );
}
