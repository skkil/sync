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

export interface PostCardStyles {
  contentClassName?: string;
  bodyClassName?: string;
}

export type PostCardVariant = 'preview' | 'detail';

const DEFAULT_CONTENT_CLASSNAME = 'space-y-4';

const POST_CARD_STYLES: Record<
  PostCardVariant,
  Partial<Record<PostType, PostCardStyles>>
> = {
  preview: {
    [PostType.LONG]: { bodyClassName: 'line-clamp-6' },
  },
  detail: {
    [PostType.SHORT]: { contentClassName: 'space-y-3' },
    [PostType.LONG]: { bodyClassName: 'line-clamp-4' },
  },
};

export function getPostCardStyles(
  variant: PostCardVariant,
  type?: PostType,
): PostCardStyles {
  const styles = (type && POST_CARD_STYLES[variant][type]) || {};

  return {
    contentClassName: styles.contentClassName ?? DEFAULT_CONTENT_CLASSNAME,
    bodyClassName: styles.bodyClassName,
  };
}
