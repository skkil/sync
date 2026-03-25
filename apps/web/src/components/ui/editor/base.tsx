import {
  CodeIcon,
  TextBolderIcon,
  TextItalicIcon,
  TextStrikethroughIcon,
  TextUnderlineIcon,
} from '@phosphor-icons/react';
import { EditorContent, useEditor } from '@tiptap/react';
import StarterKit from '@tiptap/starter-kit';
import { useTranslations } from 'next-intl';
import { forwardRef, useImperativeHandle } from 'react';

import { Button } from '@/components/ui/button';
import {
  Toolbar,
  ToolbarGroup,
} from '@/components/ui/editor/primitives/toolbar';

import { Tooltip, TooltipContent, TooltipTrigger } from '../tooltip';

function BaseViewer({ content }: { content: string }) {
  const editor = useEditor({
    extensions: [StarterKit],
    content: content ? JSON.parse(content) : '',
    editable: false,
    immediatelyRender: true,
  });

  return <EditorContent editor={editor} />;
}

interface BaseEditorRef {
  save: () => string;
  clear: () => void;
}

interface BaseEditorProps {
  content?: string;
}

const BaseEditor = forwardRef<BaseEditorRef, BaseEditorProps>(
  ({ content = '' }, ref) => {
    const t = useTranslations('components.editor');

    const editor = useEditor({
      extensions: [StarterKit],
      content,
      immediatelyRender: true,
    });

    useImperativeHandle(ref, () => ({
      save: () => {
        return JSON.stringify(editor.getJSON());
      },
      clear: () => {
        editor.commands.clearContent();
      },
    }));

    const buttons = [
      {
        id: 'bold',
        icon: <TextBolderIcon />,
        onClick: () => editor.chain().focus().toggleBold().run(),
      },
      {
        id: 'italic',
        icon: <TextItalicIcon />,
        onClick: () => editor.chain().focus().toggleItalic().run(),
      },
      {
        id: 'underline',
        icon: <TextUnderlineIcon />,
        onClick: () => editor.chain().focus().toggleUnderline().run(),
      },
      {
        id: 'strikethrough',
        icon: <TextStrikethroughIcon />,
        onClick: () => editor.chain().focus().toggleStrike().run(),
      },
      {
        id: 'code',
        icon: <CodeIcon />,
        onClick: () => editor.chain().focus().toggleCodeBlock().run(),
      },
    ];

    return (
      <div className="w-full h-full">
        <Toolbar variant="fixed">
          <ToolbarGroup>
            {buttons.map((button) => (
              <Tooltip key={button.id}>
                <TooltipTrigger asChild>
                  <Button
                    tabIndex={-1}
                    variant="ghost"
                    onClick={button.onClick}
                  >
                    {button.icon}
                  </Button>
                </TooltipTrigger>
                <TooltipContent>
                  {t(`toolbar.${button.id}.tooltip`)}
                </TooltipContent>
              </Tooltip>
            ))}
          </ToolbarGroup>
        </Toolbar>

        <EditorContent autoFocus editor={editor} className="p-2" />
      </div>
    );
  },
);
BaseEditor.displayName = 'BaseEditor';

export { BaseEditor, BaseViewer };
export type { BaseEditorRef };
