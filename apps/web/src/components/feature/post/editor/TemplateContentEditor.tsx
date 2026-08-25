'use client';

import { Placeholder } from '@tiptap/extensions';
import { EditorContent, useEditor } from '@tiptap/react';
import StarterKit from '@tiptap/starter-kit';
import { forwardRef, useImperativeHandle, useMemo } from 'react';

import { EditorBubbleMenu } from './components/EditorBubbleMenu';
import { EditorTableControls } from './components/EditorTableControls';
import { CommandsExtension } from './extensions/commands';
import { CodeBlockNode } from './extensions/nodes/code';
import { TableNode } from './extensions/nodes/table';
import { TaskItemNode, TaskListNode } from './extensions/nodes/tasks';
import { SelectAllExtension } from './extensions/select-all';
import { useCommandSearchTerms } from './extensions/use-command-search-terms';
import { deserialize, serialize } from './utils/serializer';

export interface TemplateContentEditorHandle {
  /** 미디어 없는 본문 직렬화 결과. 에디터가 아직 준비되지 않았으면 null. */
  getContent: () => { json: string; text: string } | null;
  isEmpty: () => boolean;
}

interface TemplateContentEditorProps {
  /** 수정 진입 시의 기존 본문 (Tiptap JSON 문자열). */
  initialContent?: string;
  placeholder: string;
}

/**
 * 템플릿 본문 전용 슬림 에디터. 글 에디터와 같은 스키마에서 미디어(이미지·파일·임베드)와 수식 노드를
 * 등록하지 않아, 템플릿에 미디어가 담기는 것을 스키마 수준에서 막는다 — 붙여넣기도 무력화된다.
 */
export const TemplateContentEditor = forwardRef<
  TemplateContentEditorHandle,
  TemplateContentEditorProps
>(function TemplateContentEditor({ initialContent, placeholder }, ref) {
  const commandSearchTerms = useCommandSearchTerms();

  const parsedInitialContent = useMemo(() => {
    if (!initialContent) {
      return '';
    }

    try {
      return deserialize(initialContent, []);
    } catch {
      return '';
    }
  }, [initialContent]);

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
      Placeholder.configure({ placeholder }),
      CommandsExtension.configure({
        searchTerms: commandSearchTerms,
        excludedCommands: ['image', 'file', 'embed', 'math', 'inline-math'],
      }),
      SelectAllExtension,
      CodeBlockNode,
      TaskListNode,
      TaskItemNode,
      TableNode,
    ],
    content: parsedInitialContent,
    immediatelyRender: false,
    editorProps: {
      attributes: {
        class: 'min-h-60 focus:outline-none focus:ring-0',
      },
    },
  });

  useImperativeHandle(
    ref,
    () => ({
      getContent: () => {
        if (!editor) {
          return null;
        }

        const { json, text } = serialize(editor);
        return { json, text };
      },
      isEmpty: () => editor?.isEmpty ?? true,
    }),
    [editor],
  );

  return (
    <div
      className="rounded-md border px-4 py-3"
      onClick={() => editor?.chain().focus().run()}
    >
      <div className="relative">
        <EditorContent editor={editor} />
        {editor && <EditorTableControls editor={editor} />}
      </div>
      {editor && <EditorBubbleMenu editor={editor} />}
    </div>
  );
});
