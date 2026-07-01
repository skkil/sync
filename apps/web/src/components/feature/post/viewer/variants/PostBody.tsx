'use client';

import type { Editor } from '@tiptap/react';
import { EditorContent } from '@tiptap/react';
import type { ComponentType } from 'react';

import { PostType } from '../../types/post';

export interface PostBodyVariantProps {
  editor: Editor | null;
  className?: string;
}

function DefaultPostBody({ editor, className }: PostBodyVariantProps) {
  return <EditorContent editor={editor} className={className} />;
}

const POST_BODY_VARIANTS: Record<
  PostType,
  ComponentType<PostBodyVariantProps>
> = {
  [PostType.SHORT]: DefaultPostBody,
  [PostType.LONG]: DefaultPostBody,
  [PostType.QUESTION]: DefaultPostBody,
};

interface PostBodyProps extends PostBodyVariantProps {
  type?: PostType;
}

export function PostBody({ type, ...props }: PostBodyProps) {
  const Variant = (type && POST_BODY_VARIANTS[type]) || DefaultPostBody;

  return <Variant {...props} />;
}
