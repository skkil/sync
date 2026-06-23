// TODO: Need to remove this file
import { EditorContent, type JSONContent, useEditor } from '@tiptap/react';
import StarterKit from '@tiptap/starter-kit';
import { type MouseEvent, useState } from 'react';

import {
  Dialog,
  DialogClose,
  DialogContent,
  DialogTitle,
} from '@/components/ui/dialog';
import { cn } from '@/lib/utils';

import { PostImage } from './editor/extensions/post-image';

interface PostImageNode {
  src: string;
  alt?: string;
}

function parseContent(content: string): JSONContent | string {
  try {
    return content ? (JSON.parse(content) as JSONContent) : '';
  } catch {
    return '';
  }
}

function splitImagesFromContent(content: JSONContent | string) {
  if (!content || typeof content === 'string') {
    return {
      content,
      images: [],
    };
  }

  const nodes = content.content ?? [];
  const images = nodes
    .filter(isImageNode)
    .map((node) => ({
      src: String(node.attrs?.src ?? ''),
      alt: String(node.attrs?.alt ?? ''),
    }))
    .filter((image) => image.src);

  return {
    content: {
      ...content,
      content: nodes.filter((node) => !isImageNode(node)),
    },
    images,
  };
}

function isImageNode(node: JSONContent) {
  return node.type === 'image';
}

function isOutsideRenderedImage(event: MouseEvent<HTMLImageElement>) {
  const image = event.currentTarget;
  const bounds = image.getBoundingClientRect();
  const imageRatio = image.naturalWidth / image.naturalHeight;
  const boundsRatio = bounds.width / bounds.height;

  const renderedSize =
    imageRatio > boundsRatio
      ? {
          width: bounds.width,
          height: bounds.width / imageRatio,
        }
      : {
          width: bounds.height * imageRatio,
          height: bounds.height,
        };

  const renderedBounds = {
    left: bounds.left + (bounds.width - renderedSize.width) / 2,
    right: bounds.left + (bounds.width + renderedSize.width) / 2,
    top: bounds.top + (bounds.height - renderedSize.height) / 2,
    bottom: bounds.top + (bounds.height + renderedSize.height) / 2,
  };

  return (
    event.clientX < renderedBounds.left ||
    event.clientX > renderedBounds.right ||
    event.clientY < renderedBounds.top ||
    event.clientY > renderedBounds.bottom
  );
}

function BaseViewer({ content }: { content: string }) {
  const { content: editorContent, images } = splitImagesFromContent(
    parseContent(content),
  );

  const editor = useEditor({
    extensions: [StarterKit, PostImage],
    content: editorContent,
    editable: false,
    immediatelyRender: false,
  });

  return (
    <div className="space-y-3">
      <EditorContent editor={editor} />
      <PostImageGallery images={images} />
    </div>
  );
}

function PostImageGallery({ images }: { images: PostImageNode[] }) {
  const [previewImage, setPreviewImage] = useState<PostImageNode | null>(null);

  if (images.length === 0) {
    return null;
  }

  return (
    <>
      <div
        className={cn(
          'grid gap-2 overflow-hidden rounded-lg',
          images.length === 1 ? 'grid-cols-1' : 'grid-cols-2',
        )}
      >
        {images.map((image, index) => (
          <button
            key={`${image.src}-${index}`}
            type="button"
            className="block w-full text-left"
            aria-label="Open image"
            onClick={() => setPreviewImage(image)}
          >
            <div
              className={cn(
                'relative w-full overflow-hidden rounded-lg border',
                images.length === 1 ? 'bg-background' : 'aspect-video bg-muted',
              )}
            >
              {/* eslint-disable-next-line @next/next/no-img-element */}
              <img
                src={image.src}
                alt={image.alt ?? ''}
                className={cn(
                  'block w-full cursor-zoom-in',
                  images.length > 1 && 'h-full object-cover',
                )}
              />
            </div>
          </button>
        ))}
      </div>

      <Dialog
        open={Boolean(previewImage)}
        onOpenChange={(open) => {
          if (!open) {
            setPreviewImage(null);
          }
        }}
      >
        <DialogContent
          className="fixed inset-0 top-0 left-0 flex h-dvh w-dvw max-w-none translate-x-0 translate-y-0 items-center justify-center overflow-hidden rounded-none bg-transparent p-10 ring-0 sm:max-w-none"
          onClick={(event) => {
            if (event.target === event.currentTarget) {
              setPreviewImage(null);
            }
          }}
        >
          <DialogTitle className="sr-only">Image preview</DialogTitle>
          <DialogClose className="fixed top-4 right-4 z-10 bg-background/80 hover:bg-background" />
          {previewImage && (
            // eslint-disable-next-line @next/next/no-img-element
            <img
              src={previewImage.src}
              alt={previewImage.alt ?? ''}
              className="h-full max-h-full w-full max-w-full rounded-lg object-contain"
              onClick={(event) => {
                if (isOutsideRenderedImage(event)) {
                  setPreviewImage(null);
                }
              }}
            />
          )}
        </DialogContent>
      </Dialog>
    </>
  );
}

export { BaseViewer };
