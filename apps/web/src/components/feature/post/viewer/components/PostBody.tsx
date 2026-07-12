'use client';

import type { Editor } from '@tiptap/react';
import { EditorContent } from '@tiptap/react';

export interface PostBodyProps {
  editor: Editor | null;
  className?: string;
}

export function PostBody({ editor, className }: PostBodyProps) {
  return <EditorContent editor={editor} className={className} />;
}
