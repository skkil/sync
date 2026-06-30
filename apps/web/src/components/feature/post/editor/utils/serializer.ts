import { Editor } from '@tiptap/react';
import { JSONContent } from '@tiptap/react';

import { GetPostResponseContentMediaItem } from '@/api/__generated__/types';

import { NodeType } from '../extensions/nodes';
import { ImageNodeAttributes } from '../extensions/nodes/image';

type Media = {
  id: string;
};

export function serialize(editor: Editor): {
  json: string;
  text: string;
  media: Media[];
} {
  const doc = editor.getJSON();

  const nodes = doc.content.map((node) => serializeNode(node));

  const content = nodes.map((child) => child.node);
  const media = nodes.flatMap((child) => child.media);

  return {
    json: JSON.stringify({
      ...doc,
      content,
    }),
    text: editor.getText(),
    media,
  };
}

function serializeNode(node: JSONContent): {
  node: JSONContent;
  media: Media[];
} {
  switch (node.type) {
    case NodeType.Image: {
      const { mediaId } = node.attrs as ImageNodeAttributes;

      return {
        node: {
          ...node,
          attrs: {
            mediaId,
          },
        },
        media: mediaId ? [{ id: mediaId }] : [],
      };
    }

    default: {
      return { node, media: [] };
    }
  }
}

export function deserialize(
  json: string,
  media: GetPostResponseContentMediaItem[],
): JSONContent {
  const mediaById = new Map(media.map((item) => [String(item.id), item]));

  try {
    const doc = JSON.parse(json) as JSONContent;

    const nodes = (doc.content ?? []).map((node) =>
      deserializeNode(node, mediaById),
    );

    return {
      ...doc,
      content: nodes.filter((node) => node !== null),
    };
  } catch {
    throw new Error('Failed to deserialize content');
  }
}

function deserializeNode(
  node: JSONContent,
  mediaById: Map<string, GetPostResponseContentMediaItem>,
): JSONContent | null {
  switch (node.type) {
    case NodeType.Image: {
      const { mediaId } = node.attrs as ImageNodeAttributes;

      const item = mediaId ? mediaById.get(mediaId) : undefined;

      if (!item) {
        return null;
      }

      return {
        ...node,
        attrs: {
          ...node.attrs,
          mediaId,
          src: item.url ?? null,
          status: 'uploaded',
        },
      };
    }

    default: {
      return node;
    }
  }
}
