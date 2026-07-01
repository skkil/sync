'use client';

import { CharacterCount, Placeholder } from '@tiptap/extensions';
import { EditorContent, useEditor } from '@tiptap/react';
import StarterKit from '@tiptap/starter-kit';
import { useLocale, useTranslations } from 'next-intl';
import { useEffect, useState } from 'react';

import { Badge } from '@/components/ui/badge';
import { Button } from '@/components/ui/button';
import { ScrollArea } from '@/components/ui/scroll-area';
import { Separator } from '@/components/ui/separator';
import { Tabs, TabsList, TabsTrigger } from '@/components/ui/tabs';
import { PostType } from '@/features/post/constants/post-type';

import { EditorBubbleMenu } from './components/EditorBubbleMenu';
import { EditorTemplates } from './components/EditorTemplates';
import { TagInput } from './components/TagInput';
import { CommandsExtension } from './extensions/commands';
import { ImageNode } from './extensions/nodes/image';
import { serialize } from './utils/serializer';

interface PostEditorProps {
  type: PostType;
  project?: {
    handle: string;
    name: string;
  };
  onSubmit: (data: {
    title: string;
    type: PostType;
    tags: string[];
    project?: { handle: string };
    content: {
      json: string;
      text: string;
      media: {
        id: string;
      }[];
    };
  }) => void;
}

function getContentPlaceholder(
  t: ReturnType<typeof useTranslations<'components.editor'>>,
  type: PostType,
): string {
  if (type === PostType.Short) return t('placeholders.content-short');
  if (type === PostType.Question) return t('placeholders.content-question');
  return t('placeholders.content-long');
}

export default function PostEditor({
  type: initialType,
  project,
  onSubmit,
}: PostEditorProps) {
  const t = useTranslations('components.editor');
  const tType = useTranslations('components.post.type');
  const locale = useLocale();

  const [type, setType] = useState<PostType>(initialType);
  const [title, setTitle] = useState('');
  const [tags, setTags] = useState<string[]>([]);
  const [isEditorEmpty, setIsEditorEmpty] = useState(true);

  const editor = useEditor({
    extensions: [
      StarterKit,
      Placeholder.configure({
        placeholder: getContentPlaceholder(t, type),
      }),
      CharacterCount,
      CommandsExtension,
      ImageNode,
    ],
    content: '',
    immediatelyRender: false,
    editorProps: {
      attributes: {
        class: 'focus:outline-none focus:ring-0',
      },
    },
    onUpdate: ({ editor }) => {
      setIsEditorEmpty(editor.isEmpty);
    },
  });

  useEffect(() => {
    if (!editor) return;
    const ext = editor.extensionManager.extensions.find(
      (e) => e.name === 'placeholder',
    );
    if (ext) {
      ext.options.placeholder = getContentPlaceholder(t, type);
      editor.view.dispatch(editor.state.tr);
    }
  }, [type, editor, t]);

  const handleSubmit = () => {
    if (!editor) {
      return;
    }

    onSubmit({
      title,
      type,
      tags,
      project: project ? { handle: project.handle } : undefined,
      content: serialize(editor),
    });
  };

  const showTitle = type !== PostType.Short;
  const titlePlaceholder =
    type === PostType.Question
      ? t('placeholders.title-question')
      : t('placeholders.title-long');

  return (
    <div className="flex flex-col h-full">
      <div className="flex-1 overflow-y-auto py-12 flex flex-col gap-4">
        {project && (
          <Badge variant="secondary" className="w-fit">
            {t('posting-to', { project: project.name })}
          </Badge>
        )}

        <Tabs
          value={type}
          onValueChange={(value) => {
            setType(value as PostType);
          }}
        >
          <TabsList>
            {Object.values(PostType).map((pt) => (
              <TabsTrigger key={pt} value={pt}>
                {tType(pt)}
              </TabsTrigger>
            ))}
          </TabsList>
        </Tabs>

        {showTitle && (
          <input
            className="w-full shrink-0 resize-none bg-transparent text-4xl font-bold outline-none placeholder:text-muted-foreground leading-tight"
            placeholder={titlePlaceholder}
            value={title}
            onChange={(e) => setTitle(e.target.value)}
          />
        )}

        <ScrollArea className="flex-1 min-h-0 max-h-screen">
          <EditorContent editor={editor} />
          {editor && <EditorBubbleMenu editor={editor} />}
          {isEditorEmpty && type === PostType.Long && (
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
        <TagInput tags={tags} onChange={setTags} />
        <div className="flex justify-end">
          <Button onClick={handleSubmit}>{t('submit')}</Button>
        </div>
      </div>
    </div>
  );
}
