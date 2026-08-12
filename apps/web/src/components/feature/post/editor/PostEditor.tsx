'use client';

import { ArrowLeftIcon, FolderIcon } from '@phosphor-icons/react';
import { TableOfContents } from '@tiptap/extension-table-of-contents';
import { CharacterCount, Placeholder } from '@tiptap/extensions';
import { EditorContent, useEditor } from '@tiptap/react';
import StarterKit from '@tiptap/starter-kit';
import { useLocale, useTranslations } from 'next-intl';
import { useRouter } from 'next/navigation';
import { useEffect, useMemo, useRef, useState } from 'react';
import { toast } from 'sonner';

import type { GetPostResponseContent } from '@/api/__generated__/types';
import { TwoColumnLayout } from '@/components/layout/TwoColumnLayout';
import { Badge } from '@/components/ui/badge';
import { Button, LinkButton } from '@/components/ui/button';
import { useSession } from '@/lib/auth/client';
import { ErrorCode } from '@/lib/error';
import { isContentPlaceholderVisible } from '@/lib/tiptap-utils';
import { cn } from '@/lib/utils';
import ROUTES from '@/util/routes';

import { QUESTION_TITLE_PREFIX } from '../constants';
import {
  PostContentFormat,
  PostScope,
  PostStatus,
  PostType,
} from '../types/post';
import type { PostTocItem } from '../viewer/PostContext';
import { PostDeleteButton } from '../viewer/components/PostDeleteButton';
import { PostTableOfContentsList } from '../viewer/components/PostTableOfContentsList';
import { PostSummary } from '../viewer/types';
import { EditorBubbleMenu } from './components/EditorBubbleMenu';
import { EditorTableControls } from './components/EditorTableControls';
import { EditorTemplates } from './components/EditorTemplates';
import { MathDialog } from './components/MathDialog';
import { PostTypeSelector } from './components/PostTypeSelector';
import { SeriesSelect, SeriesSelection } from './components/SeriesSelect';
import { TagInput, type TagInputHandle, TagValue } from './components/TagInput';
import { CoverPicker } from './cover/CoverPicker';
import { type CoverState, initialCoverState } from './cover/coverState';
import { renderCoverToFile } from './cover/generators';
import { useCoverImageUpload } from './cover/useCoverImageUpload';
import {
  COMMAND_NAMES,
  CommandSearchTerms,
  CommandsExtension,
} from './extensions/commands';
import { MediaDropPasteExtension } from './extensions/media-drop';
import { NodeType } from './extensions/nodes';
import { CodeBlockNode } from './extensions/nodes/code';
import { EmbedNode } from './extensions/nodes/embed';
import { FileNode } from './extensions/nodes/file';
import { ImageNode } from './extensions/nodes/image';
import { type MathTarget, createMathNode } from './extensions/nodes/math';
import { TableNode } from './extensions/nodes/table';
import { TaskItemNode, TaskListNode } from './extensions/nodes/tasks';
import { SelectAllExtension } from './extensions/select-all';
import { markdownToHtml } from './utils/markdown';
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
  /** 편집 진입 시점에 게시글이 속한 시리즈. 시리즈 선택기의 초기값이 된다. */
  initialSeries?: {
    seriesId: string;
    seriesName: string;
  } | null;
  isSubmitting?: boolean;
  onSubmit: (data: {
    title: string;
    type: PostType;
    status: PostStatus;
    tags: string[];
    projectTags: string[];
    series: SeriesSelection | null;
    coverMediaId?: string;
    removeCover?: boolean;
    content: {
      json: string;
      text: string;
      media: {
        id: string;
      }[];
    };
  }) => void;
}

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
  initialSeries,
  isSubmitting = false,
  onSubmit,
}: PostEditorProps) {
  const t = useTranslations('components.editor');
  const locale = useLocale();
  const router = useRouter();
  const { data: session } = useSession();

  const postId = summary?.id;
  const slug = summary?.slug;
  const initialTitle = summary?.title;
  const initialStatus = summary?.status ?? PostStatus.PUBLISHED;
  const initialScope = summary?.scope;

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
  const tagsRef = useRef(tags);
  const tagInputRef = useRef<TagInputHandle>(null);
  const submitTagCommitFailedRef = useRef(false);
  const handleTagsChange = (nextTags: TagValue[]) => {
    tagsRef.current = nextTags;
    setTags(nextTags);
  };
  const commitPendingTag = () => {
    return tagInputRef.current?.commitPending() ?? true;
  };
  const handleSubmitPointerDown = () => {
    submitTagCommitFailedRef.current = !commitPendingTag();
  };
  const [series, setSeries] = useState<SeriesSelection | null>(() =>
    initialSeries
      ? {
          kind: 'existing',
          externalId: initialSeries.seriesId,
          name: initialSeries.seriesName,
        }
      : null,
  );
  const hadInitialCover = Boolean(summary?.coverImageUrl);
  const [cover, setCover] = useState<CoverState>(() =>
    initialCoverState(summary?.coverImageUrl),
  );
  const { upload: uploadCover } = useCoverImageUpload();
  const [isPreparingCover, setIsPreparingCover] = useState(false);
  const [isEditorEmpty, setIsEditorEmpty] = useState(true);
  const [isPlaceholderVisible, setIsPlaceholderVisible] = useState(true);
  const [validationMessage, setValidationMessage] = useState<string | null>(
    null,
  );
  const [tocItems, setTocItems] = useState<PostTocItem[]>([]);
  const [mathTarget, setMathTarget] = useState<MathTarget | null>(null);
  const titleRef = useRef<HTMLTextAreaElement>(null);

  useEffect(() => {
    const el = titleRef.current;
    if (!el) return;
    el.style.height = 'auto';
    el.style.height = `${el.scrollHeight}px`;
  }, [title, type]);

  const commandSearchTerms = useMemo<CommandSearchTerms>(
    () =>
      Object.fromEntries(
        COMMAND_NAMES.map((name) => [
          name,
          [
            t(`commands.${name}.title`),
            ...t(`commands.${name}.keywords`).split(/\s+/),
          ],
        ]),
      ),
    [t],
  );

  // 본문 형식은 두 가지다. 지금까지의 모든 글은 Tiptap JSON 이고, 에이전트가 만들어 아직 한 번도
  // 저장되지 않은 초안만 Markdown 이다. 후자는 HTML 로 바꿔서 넘기면 Tiptap 이 자기 스키마로
  // 읽어 들이고, 이어지는 첫 저장이 Tiptap JSON 을 남기면서 형식 차이가 사라진다.
  const initialContent = useMemo(() => {
    if (!content) {
      return '';
    }

    if (content.format === PostContentFormat.MARKDOWN) {
      return content.markdown ? markdownToHtml(content.markdown) : '';
    }

    if (!content.json) {
      return '';
    }

    try {
      return deserialize(content.json, content.media);
    } catch {
      return '';
    }
  }, [content]);

  const editor = useEditor({
    extensions: [
      StarterKit.configure({
        codeBlock: false,
        link: {
          openOnClick: false,
          autolink: true,
          defaultProtocol: 'https',
        },
      }),
      Placeholder.configure({
        placeholder: getContentPlaceholder(t, type),
      }),
      CharacterCount,
      CommandsExtension.configure({
        searchTerms: commandSearchTerms,
        actions: { openMathEditor: setMathTarget },
      }),
      SelectAllExtension,
      CodeBlockNode,
      TaskListNode,
      TaskItemNode,
      ImageNode,
      FileNode.configure({ slug: slug ?? null }),
      EmbedNode,
      TableNode,
      createMathNode(setMathTarget),
      MediaDropPasteExtension,
      TableOfContents.configure({
        onUpdate: (items) =>
          setTocItems(
            items.map((item) => ({
              id: item.id,
              level: item.level,
              itemIndex: item.itemIndex,
              textContent: item.textContent,
              isActive: item.isActive,
            })),
          ),
      }),
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
      setIsPlaceholderVisible(isContentPlaceholderVisible(editor));
    },
    onCreate: ({ editor }) => {
      setIsEditorEmpty(editor.isEmpty);
      setIsPlaceholderVisible(isContentPlaceholderVisible(editor));
    },
  });

  /**
   * 저장되지 않은 내용이 있는 상태에서 새로고침/탭 닫기/외부 이동 시
   * 브라우저 기본 확인 창을 띄워 데이터 유실을 방지한다.
   */
  const hasUnsavedContent =
    !isEditorEmpty || title.trim().length > 0 || tags.length > 0;

  useEffect(() => {
    if (!hasUnsavedContent) return;

    const handleBeforeUnload = (event: BeforeUnloadEvent) => {
      event.preventDefault();
      event.returnValue = '';
    };

    window.addEventListener('beforeunload', handleBeforeUnload);
    return () => {
      window.removeEventListener('beforeunload', handleBeforeUnload);
    };
  }, [hasUnsavedContent]);

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

  const handleMathSubmit = (target: MathTarget, latex: string) => {
    if (!editor) return;

    const { pos } = target;

    if (target.type === NodeType.BlockMath) {
      if (target.isNew) {
        editor.chain().focus().insertBlockMath({ latex, pos }).run();
      } else {
        editor.chain().focus().updateBlockMath({ latex, pos }).run();
      }
    } else if (target.isNew) {
      editor.chain().focus().insertInlineMath({ latex, pos }).run();
    } else {
      editor.chain().focus().updateInlineMath({ latex, pos }).run();
    }

    setMathTarget(null);
  };

  const handleMathDelete = (target: MathTarget) => {
    if (!editor) return;

    const { pos } = target;

    if (target.type === NodeType.BlockMath) {
      editor.chain().focus().deleteBlockMath({ pos }).run();
    } else {
      editor.chain().focus().deleteInlineMath({ pos }).run();
    }

    setMathTarget(null);
  };

  /**
   * Resolve the cover fields for submission. A gallery-picked cover is only
   * rendered and uploaded here — at save time — never when it was selected.
   * `ok: false` means a required cover upload failed (caller should abort);
   * `code` carries the reason when the failure was a `SyncError`.
   */
  const resolveCoverSubmit = async (): Promise<
    | { ok: true; coverMediaId?: string; removeCover?: boolean }
    | { ok: false; code?: ErrorCode }
  > => {
    // Covers belong to blog (LONG) posts only; anything else drops its cover.
    if (type !== PostType.LONG) {
      return hadInitialCover ? { ok: true, removeCover: true } : { ok: true };
    }

    if (cover.kind === 'generated' || cover.kind === 'uploaded') {
      const file =
        cover.kind === 'generated'
          ? await renderCoverToFile(cover.params)
          : cover.file;
      const result = await uploadCover(file);
      if (!result.ok) return { ok: false, code: result.code };
      return { ok: true, coverMediaId: result.mediaId };
    }
    if (cover.kind === 'none' && hadInitialCover) {
      return { ok: true, removeCover: true };
    }
    return { ok: true };
  };

  const handleSubmit = async (status: PostStatus) => {
    if (!editor || isPreparingCover) {
      return;
    }

    const pointerCommitFailed = submitTagCommitFailedRef.current;
    submitTagCommitFailedRef.current = false;
    if (pointerCommitFailed) {
      return;
    }

    if (editor.isEmpty) {
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

    setValidationMessage(null);
    if (!commitPendingTag()) {
      return;
    }
    const submitTags = tagsRef.current;

    setIsPreparingCover(true);
    let coverResult: Awaited<ReturnType<typeof resolveCoverSubmit>>;
    try {
      coverResult = await resolveCoverSubmit();
    } catch {
      coverResult = { ok: false };
    } finally {
      setIsPreparingCover(false);
    }

    if (!coverResult.ok) {
      switch (coverResult.code) {
        case ErrorCode.MEDIA_TOO_LARGE:
          toast.error(t('cover.errors.max-size'));
          break;
        case ErrorCode.UNSUPPORTED_MEDIA_TYPE:
          toast.error(t('cover.errors.unsupported-type'));
          break;
        default:
          toast.error(t('cover.errors.upload-failed'));
      }
      return;
    }

    const { coverMediaId, removeCover } = coverResult;

    onSubmit({
      title,
      type,
      status,
      tags: submitTags
        .filter((tag) => !tag.isProjectTag)
        .map((tag) => tag.name),
      projectTags: submitTags
        .filter((tag) => tag.isProjectTag)
        .map((tag) => tag.name),
      series,
      coverMediaId,
      removeCover,
      content: serialize(editor),
    });
  };

  // 편집 페이지는 언제나 특정 게시글을 고치러 들어오므로, 뒤로가기는 브라우저
  // 히스토리(router.back)가 아니라 확정된 목적지로 이동한다. 발행된 글이면 그
  // 글의 보기 페이지로, 임시저장(DRAFT) 이면 공개 보기 페이지가 없으므로
  // 임시저장 목록으로 돌아간다.
  const isDraft = initialStatus === PostStatus.DRAFT;
  const backHref = isDraft
    ? project?.handle
      ? ROUTES.PROJECT_DRAFTS(project.handle)
      : session?.user.handle
        ? ROUTES.PROFILE_DRAFTS(session.user.handle)
        : ROUTES.HOME()
    : project?.handle && slug
      ? ROUTES.PROJECT_POST(project.handle, slug)
      : slug
        ? ROUTES.POST(slug)
        : ROUTES.HOME();
  const backLabel = isDraft
    ? t('actions.back-to-drafts')
    : t('actions.back-to-post');

  const showTitle = type !== PostType.SHORT;
  const coverSeed = title.trim() || slug || 'sync-cover';
  const titlePlaceholder =
    type === PostType.QUESTION
      ? t('placeholders.title-question')
      : t('placeholders.title-long');
  const scopeLabel = project
    ? t('scope.project', { project: project.name })
    : initialScope === PostScope.WORKSPACE
      ? t('scope.project-generic')
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
      {isEditing && (
        <LinkButton
          href={backHref}
          variant="ghost"
          size="sm"
          className="-ml-2 self-start"
        >
          <ArrowLeftIcon />
          {backLabel}
        </LinkButton>
      )}

      {type === PostType.LONG && (
        <CoverPicker
          value={cover}
          onChange={setCover}
          defaultSeed={coverSeed}
        />
      )}

      {showTitle && (
        <div className="flex w-full shrink-0 items-start gap-2">
          {/* 질문은 카드에서도 같은 표식을 달고 나가므로, 쓰는 동안에도 같은
              모습으로 보여준다. 표식만 굵고 제목은 본문 굵기다. */}
          {type === PostType.QUESTION && (
            <span className="shrink-0 text-4xl font-bold leading-tight">
              {QUESTION_TITLE_PREFIX}
            </span>
          )}

          <textarea
            ref={titleRef}
            rows={1}
            className={cn(
              'min-w-0 flex-1 resize-none overflow-hidden bg-transparent text-4xl outline-none placeholder:text-muted-foreground/50 leading-tight break-words',
              type === PostType.QUESTION ? 'font-normal' : 'font-bold',
            )}
            placeholder={titlePlaceholder}
            value={title}
            onChange={(e) => setTitle(e.target.value)}
            onKeyDown={(e) => {
              if (e.key === 'Enter') {
                e.preventDefault();
                editor?.chain().focus('start').run();
              }
            }}
          />
        </div>
      )}

      <div className={cn(type === PostType.SHORT && 'text-lg')}>
        <div className="relative">
          <EditorContent editor={editor} />
          {editor && <EditorTableControls editor={editor} />}
        </div>
        {editor && <EditorBubbleMenu editor={editor} />}
        <MathDialog
          target={mathTarget}
          onSubmit={handleMathSubmit}
          onDelete={handleMathDelete}
          onClose={() => setMathTarget(null)}
        />
        {isPlaceholderVisible && type === PostType.LONG && (
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
      <PostTableOfContentsList items={tocItems} />

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
          ref={tagInputRef}
          tags={tags}
          onChange={handleTagsChange}
          projectHandle={project?.handle}
          accentRing={ACCENT_RING[type]}
        />
      </section>

      <section className="flex flex-col gap-2">
        <h3 className="px-1 text-xs font-semibold uppercase tracking-wide text-muted-foreground">
          {t('sidebar.series')}
        </h3>
        <div data-tag-commit-target="" onPointerDownCapture={commitPendingTag}>
          <SeriesSelect
            value={series}
            onChange={setSeries}
            projectHandle={project?.handle}
            accentRing={ACCENT_RING[type]}
          />
        </div>
      </section>
    </div>
  );

  const sideFooter = (
    <div className="mt-6 flex flex-col gap-4 lg:mt-0 lg:border-t lg:border-border lg:bg-background lg:pt-4">
      <div
        className={cn(
          'grid gap-2',
          canSaveDraft ? 'grid-cols-2' : 'grid-cols-1',
        )}
      >
        {canSaveDraft && (
          <Button
            data-tag-commit-target=""
            variant="outline"
            disabled={isSubmitting || isPreparingCover || isEditorEmpty}
            onPointerDownCapture={handleSubmitPointerDown}
            onClick={() => handleSubmit(PostStatus.DRAFT)}
          >
            {draftActionLabel}
          </Button>
        )}
        <Button
          data-tag-commit-target=""
          disabled={isSubmitting || isPreparingCover || isEditorEmpty}
          onPointerDownCapture={handleSubmitPointerDown}
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
              redirectTo={
                session?.user.handle
                  ? ROUTES.PROFILE_DRAFTS(session.user.handle)
                  : ROUTES.HOME()
              }
              className="w-full"
            />
          </div>
        )}
    </div>
  );

  return (
    <TwoColumnLayout
      main={main}
      side={side}
      sideFooter={sideFooter}
      sideViewportScrollable
    />
  );
}
