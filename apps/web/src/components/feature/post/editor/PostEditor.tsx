'use client';

import { FolderIcon } from '@phosphor-icons/react';
import { CharacterCount, Placeholder } from '@tiptap/extensions';
import { EditorContent, useEditor } from '@tiptap/react';
import StarterKit from '@tiptap/starter-kit';
import { useLocale, useTranslations } from 'next-intl';
import { useRouter } from 'next/navigation';
import { useEffect, useMemo, useRef, useState } from 'react';
import { toast } from 'sonner';

import type {
  GetPostResponseContent,
  GetPostResponseContentMediaItem,
} from '@/api/__generated__/types';
import { TwoColumnLayout } from '@/components/layout/TwoColumnLayout';
import { Badge } from '@/components/ui/badge';
import { Button } from '@/components/ui/button';
import { cn } from '@/lib/utils';
import ROUTES from '@/util/routes';

import { PostScope, PostStatus, PostType } from '../types/post';
import { PostDeleteButton } from '../viewer/components/PostDeleteButton';
import { PostSummary } from '../viewer/types';
import { EditorBubbleMenu } from './components/EditorBubbleMenu';
import { EditorTemplates } from './components/EditorTemplates';
import { PostTypeSelector } from './components/PostTypeSelector';
import { TagInput, TagValue } from './components/TagInput';
import { CommandsExtension } from './extensions/commands';
import { ImageNode } from './extensions/nodes/image';
import { deserialize, serialize } from './utils/serializer';

interface PostEditorProps {
  isEditing?: boolean;
  type: PostType;
  routeProjectHandle?: string;
  summary?: PostSummary;
  content?: GetPostResponseContent;
  project?: {
    handle: string;
    name: string;
  };
  isSubmitting?: boolean;
  onSubmit: (data: {
    title: string;
    type: PostType;
    status: PostStatus;
    tags: string[];
    projectTags: string[];
    content: {
      json: string;
      text: string;
      media: {
        id: string;
      }[];
    };
  }) => void;
}

const EMPTY_MEDIA: GetPostResponseContentMediaItem[] = [];

const ACCENT_RING: Record<PostType, string> = {
  [PostType.SHORT]: 'focus-within:ring-primary/30',
  [PostType.LONG]: 'focus-within:ring-blue-500/30',
  [PostType.QUESTION]: 'focus-within:ring-amber-600/30',
};

function getContentPlaceholder(
  t: ReturnType<typeof useTranslations<'components.editor'>>,
  type: PostType,
): string {
  if (type === PostType.SHORT) return t('placeholders.content-short');
  if (type === PostType.QUESTION) return t('placeholders.content-question');
  return t('placeholders.content-long');
}

export default function PostEditor({
  isEditing = false,
  type: initialType,
  routeProjectHandle,
  summary,
  content,
  project,
  isSubmitting = false,
  onSubmit,
}: PostEditorProps) {
  const t = useTranslations('components.editor');
  const locale = useLocale();
  const router = useRouter();

  const postId = summary?.id;
  const slug = summary?.slug;
  const initialTitle = summary?.title;
  const initialStatus = summary?.status ?? PostStatus.PUBLISHED;
  const initialScope = summary?.scope;
  const initialContentJson = content?.json;
  const initialMedia = content?.media ?? EMPTY_MEDIA;

  useEffect(() => {
    if (!isEditing || !slug) {
      return;
    }

    const canonicalPath = project?.handle
      ? ROUTES.PROJECT_POST_EDIT(project.handle, slug)
      : ROUTES.POST_EDIT(slug);
    const isCanonical = project?.handle
      ? routeProjectHandle === project.handle
      : routeProjectHandle === undefined;

    if (!isCanonical) {
      router.replace(canonicalPath);
    }
  }, [isEditing, project?.handle, router, routeProjectHandle, slug]);

  const [type, setType] = useState<PostType>(initialType);
  const [title, setTitle] = useState(initialTitle ?? '');
  const [tags, setTags] = useState<TagValue[]>(() => [
    ...(summary?.tags ?? [])
      .filter((tag) => !tag.projectHandle)
      .map((tag) => ({ name: tag.name, isProjectTag: false })),
    ...(summary?.tags ?? [])
      .filter((tag) => tag.projectHandle)
      .map((tag) => ({ name: tag.name, isProjectTag: true })),
  ]);
  const [isEditorEmpty, setIsEditorEmpty] = useState(true);
  const [validationMessage, setValidationMessage] = useState<string | null>(
    null,
  );
  const titleRef = useRef<HTMLTextAreaElement>(null);

  useEffect(() => {
    const el = titleRef.current;
    if (!el) return;
    el.style.height = 'auto';
    el.style.height = `${el.scrollHeight}px`;
  }, [title]);

  const initialContent = useMemo(() => {
    if (!initialContentJson) {
      return '';
    }

    try {
      return deserialize(initialContentJson, initialMedia);
    } catch {
      return '';
    }
  }, [initialContentJson, initialMedia]);

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
    content: initialContent,
    immediatelyRender: false,
    editorProps: {
      attributes: {
        class: 'focus:outline-none focus:ring-0',
      },
    },
    onUpdate: ({ editor }) => {
      setIsEditorEmpty(editor.isEmpty);
    },
    onCreate: ({ editor }) => {
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

  const handleSubmit = (status: PostStatus) => {
    if (!editor) {
      return;
    }

    if (editor.getText().trim().length === 0) {
      toast.error(t('messages.empty-content'));
      return;
    }

    if (
      status === PostStatus.PUBLISHED &&
      type !== PostType.SHORT &&
      title.trim().length === 0
    ) {
      setValidationMessage(t('validation.title-required'));
      return;
    }

    if (status === PostStatus.PUBLISHED && tags.length === 0) {
      setValidationMessage(t('validation.tags-required'));
      return;
    }

    setValidationMessage(null);

    onSubmit({
      title,
      type,
      status,
      tags: tags.filter((tag) => !tag.isProjectTag).map((tag) => tag.name),
      projectTags: tags
        .filter((tag) => tag.isProjectTag)
        .map((tag) => tag.name),
      content: serialize(editor),
    });
  };

  const showTitle = type !== PostType.SHORT;
  const titlePlaceholder =
    type === PostType.QUESTION
      ? t('placeholders.title-question')
      : t('placeholders.title-long');
  const scopeLabel = project
    ? t('scope.workspace', { workspace: project.name })
    : initialScope === PostScope.WORKSPACE
      ? t('scope.workspace-generic')
      : t('scope.public');
  const canSaveDraft = !isEditing || initialStatus === PostStatus.DRAFT;
  const draftActionLabel = isEditing
    ? t('actions.update-draft')
    : t('actions.save-draft');
  const publishActionLabel =
    isEditing && initialStatus === PostStatus.PUBLISHED
      ? t('actions.update-published')
      : t('actions.publish');

  const main = (
    <div
      className={cn(
        'flex w-full flex-col gap-4 py-6',
        type === PostType.SHORT && 'mx-auto max-w-xl pt-10',
      )}
    >
      {showTitle && (
        <textarea
          ref={titleRef}
          rows={1}
          className="w-full shrink-0 resize-none overflow-hidden bg-transparent text-4xl font-bold outline-none placeholder:text-muted-foreground/50 leading-tight break-words"
          placeholder={titlePlaceholder}
          value={title}
          onChange={(e) => setTitle(e.target.value)}
          onKeyDown={(e) => {
            if (e.key === 'Enter') {
              e.preventDefault();
              editor?.commands.focus();
            }
          }}
        />
      )}

      <div className={cn(type === PostType.SHORT && 'text-lg')}>
        <EditorContent editor={editor} />
        {editor && <EditorBubbleMenu editor={editor} />}
        {isEditorEmpty && type === PostType.LONG && (
          <EditorTemplates
            locale={locale}
            onSelect={(template) => {
              setTitle(template.title);
              editor?.commands.setContent(template.content);
            }}
          />
        )}
      </div>

      {validationMessage && (
        <p className="text-sm text-destructive">{validationMessage}</p>
      )}
    </div>
  );

  const side = (
    <div className="flex flex-col gap-6">
      <Badge variant="secondary" className="w-fit">
        {scopeLabel}
      </Badge>

      <section className="flex flex-col gap-2">
        <h3 className="px-1 text-xs font-semibold uppercase tracking-wide text-muted-foreground">
          {t('sidebar.type')}
        </h3>
        <PostTypeSelector value={type} onChange={setType} />
      </section>

      {project && (
        <section className="flex flex-col gap-2">
          <h3 className="px-1 text-xs font-semibold uppercase tracking-wide text-muted-foreground">
            {t('sidebar.project')}
          </h3>
          <div className="flex items-center gap-2 rounded-lg border border-border bg-background px-3 py-2.5">
            <span className="flex size-7 shrink-0 items-center justify-center rounded-md bg-muted text-muted-foreground">
              <FolderIcon size={16} />
            </span>
            <span className="truncate text-sm font-medium">{project.name}</span>
          </div>
        </section>
      )}

      <section className="flex flex-col gap-2">
        <h3 className="px-1 text-xs font-semibold uppercase tracking-wide text-muted-foreground">
          {t('sidebar.tags')}
        </h3>
        <TagInput
          tags={tags}
          onChange={setTags}
          projectHandle={project?.handle}
          accentRing={ACCENT_RING[type]}
        />
      </section>

      <div
        className={cn(
          'grid gap-2',
          canSaveDraft ? 'grid-cols-2' : 'grid-cols-1',
        )}
      >
        {canSaveDraft && (
          <Button
            variant="outline"
            disabled={isSubmitting || isEditorEmpty}
            onClick={() => handleSubmit(PostStatus.DRAFT)}
          >
            {draftActionLabel}
          </Button>
        )}
        <Button
          disabled={isSubmitting || isEditorEmpty}
          onClick={() => handleSubmit(PostStatus.PUBLISHED)}
        >
          {publishActionLabel}
        </Button>
      </div>

      {isEditing &&
        initialStatus === PostStatus.DRAFT &&
        postId !== undefined && (
          <div className="border-t pt-4">
            <PostDeleteButton
              postId={postId}
              redirectTo={ROUTES.DRAFTS()}
              className="w-full"
            />
          </div>
        )}
    </div>
  );

  return <TwoColumnLayout main={main} side={side} />;
}
