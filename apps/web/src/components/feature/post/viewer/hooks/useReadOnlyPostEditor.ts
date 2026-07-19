import type { JSONContent } from '@tiptap/react';
import { useEditor } from '@tiptap/react';
import StarterKit from '@tiptap/starter-kit';

import type { GetPostResponse } from '@/api/__generated__/types';

import { ReadOnlyImageNode } from '../../editor/extensions/nodes/image';
import { deserialize } from '../../editor/utils/serializer';

const EMPTY_DOC: JSONContent = { type: 'doc', content: [] };

export function useReadOnlyPostEditor(
  content: Pick<GetPostResponse['content'], 'json' | 'media'>,
) {
  let doc: JSONContent;
  try {
    doc = deserialize(content.json, content.media);
  } catch (error) {
    console.error('Failed to parse post content', error);
    doc = EMPTY_DOC;
  }

  return useEditor({
    extensions: [StarterKit, ReadOnlyImageNode],
    content: doc,
    editable: false,
    immediatelyRender: false,
  });
}
