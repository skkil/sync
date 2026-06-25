'use client';

import {
  CodeIcon,
  TextBolderIcon,
  TextHOneIcon,
  TextHTwoIcon,
  TextItalicIcon,
  TextStrikethroughIcon,
} from '@phosphor-icons/react';
import { useEditor } from '@tiptap/react';
import { BubbleMenu } from '@tiptap/react/menus';

import { Separator } from '@/components/ui/separator';
import { cn } from '@/lib/utils';

import { NodeType } from '../extensions/nodes';

interface EditorBubbleMenuProps {
  editor: ReturnType<typeof useEditor>;
}

export function EditorBubbleMenu({ editor }: EditorBubbleMenuProps) {
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
      shouldShow={({ editor, view }) => {
        if (!view.hasFocus()) {
          return false;
        }

        if (editor.isActive(NodeType.Image)) {
          return false;
        }

        return true;
      }}
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
            aria-label={btn.id}
          >
            {btn.icon}
          </button>
        ),
      )}
    </BubbleMenu>
  );
}
