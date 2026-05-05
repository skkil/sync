import {
  CaretLeftIcon,
  CaretRightIcon,
  CodeIcon,
  TextBolderIcon,
  TextItalicIcon,
  TextStrikethroughIcon,
  TextUnderlineIcon,
} from '@phosphor-icons/react';
import { EditorContent, type JSONContent, useEditor } from '@tiptap/react';
import StarterKit from '@tiptap/starter-kit';
import { useTranslations } from 'next-intl';
import {
  type MouseEvent,
  forwardRef,
  useImperativeHandle,
  useState,
} from 'react';

import { Button } from '@/components/ui/button';
import {
  Dialog,
  DialogClose,
  DialogContent,
  DialogTitle,
} from '@/components/ui/dialog';
import {
  Toolbar,
  ToolbarGroup,
} from '@/components/ui/editor/primitives/toolbar';
import { cn } from '@/lib/utils';

import { Tooltip, TooltipContent, TooltipTrigger } from '../tooltip';
import { PostImage } from './extensions/post-image';

interface PostImageNode {
  src: string;
  alt?: string;
}

type PreviewDirection = 'previous' | 'next';

interface OutgoingPreviewImage {
  image: PostImageNode;
  index: number;
  direction: PreviewDirection;
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
  const [previewImageIndex, setPreviewImageIndex] = useState<number | null>(
    null,
  );
  const [previewDirection, setPreviewDirection] =
    useState<PreviewDirection | null>(null);
  const [outgoingPreviewImage, setOutgoingPreviewImage] =
    useState<OutgoingPreviewImage | null>(null);

  if (images.length === 0) {
    return null;
  }

  const previewImage =
    previewImageIndex === null ? null : images[previewImageIndex];
  const hasPreviousImage = previewImageIndex !== null && previewImageIndex > 0;
  const hasNextImage =
    previewImageIndex !== null && previewImageIndex < images.length - 1;

  const closePreview = () => {
    setPreviewDirection(null);
    setOutgoingPreviewImage(null);
    setPreviewImageIndex(null);
  };

  const openPreview = (index: number) => {
    setPreviewDirection(null);
    setOutgoingPreviewImage(null);
    setPreviewImageIndex(index);
  };

  const movePreview = (direction: PreviewDirection) => {
    if (previewImageIndex === null) {
      return;
    }

    const currentImage = images[previewImageIndex];
    if (!currentImage) {
      return;
    }

    const nextIndex =
      direction === 'previous'
        ? Math.max(previewImageIndex - 1, 0)
        : Math.min(previewImageIndex + 1, images.length - 1);

    if (nextIndex === previewImageIndex) {
      return;
    }

    setOutgoingPreviewImage({
      image: currentImage,
      index: previewImageIndex,
      direction,
    });
    setPreviewDirection(direction);
    setPreviewImageIndex(nextIndex);
  };

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
            onClick={() => openPreview(index)}
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
                  images.length === 1 && 'max-h-[520px] object-contain',
                  images.length > 1 && 'h-full object-cover',
                )}
              />
            </div>
          </button>
        ))}
      </div>

      <Dialog
        open={previewImageIndex !== null}
        onOpenChange={(open) => {
          if (!open) {
            closePreview();
          }
        }}
      >
        <DialogContent
          className="fixed inset-0 top-0 left-0 flex h-dvh w-dvw max-w-none translate-x-0 translate-y-0 items-center justify-center overflow-hidden rounded-none bg-transparent p-10 ring-0 sm:max-w-none"
          onClick={(event) => {
            if (event.target === event.currentTarget) {
              closePreview();
            }
          }}
        >
          <DialogTitle className="sr-only">Image preview</DialogTitle>
          <DialogClose className="fixed top-4 right-4 z-10 bg-background/80 hover:bg-background" />
          {images.length > 1 && hasPreviousImage && (
            <Button
              type="button"
              variant="secondary"
              size="icon-lg"
              className="fixed top-1/2 left-4 z-10 -translate-y-1/2 bg-background/80 hover:bg-background"
              aria-label="Previous image"
              onClick={() => movePreview('previous')}
            >
              <CaretLeftIcon weight="bold" />
            </Button>
          )}

          {images.length > 1 && hasNextImage && (
            <Button
              type="button"
              variant="secondary"
              size="icon-lg"
              className="fixed top-1/2 right-4 z-10 -translate-y-1/2 bg-background/80 hover:bg-background"
              aria-label="Next image"
              onClick={() => movePreview('next')}
            >
              <CaretRightIcon weight="bold" />
            </Button>
          )}
          <div className="relative h-full max-h-full w-full max-w-full overflow-hidden">
            {outgoingPreviewImage && (
              // eslint-disable-next-line @next/next/no-img-element
              <img
                key={`outgoing-${outgoingPreviewImage.image.src}-${outgoingPreviewImage.index}`}
                src={outgoingPreviewImage.image.src}
                alt={outgoingPreviewImage.image.alt ?? ''}
                className={cn(
                  'pointer-events-none absolute inset-0 z-10 h-full w-full rounded-lg object-contain',
                  outgoingPreviewImage.direction === 'next' &&
                    'post-image-preview-exit-next',
                  outgoingPreviewImage.direction === 'previous' &&
                    'post-image-preview-exit-previous',
                )}
                onAnimationEnd={() => setOutgoingPreviewImage(null)}
              />
            )}

            {previewImage && (
              // eslint-disable-next-line @next/next/no-img-element
              <img
                key={`${previewImage.src}-${previewImageIndex}`}
                src={previewImage.src}
                alt={previewImage.alt ?? ''}
                className={cn(
                  'absolute inset-0 h-full w-full rounded-lg object-contain',
                  previewDirection === 'next' &&
                    'post-image-preview-enter-next',
                  previewDirection === 'previous' &&
                    'post-image-preview-enter-previous',
                )}
                onClick={(event) => {
                  if (isOutsideRenderedImage(event)) {
                    closePreview();
                  }
                }}
              />
            )}
          </div>
        </DialogContent>
      </Dialog>
    </>
  );
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
      extensions: [StarterKit, PostImage],
      content: parseContent(content),
      immediatelyRender: false,
    });

    useImperativeHandle(ref, () => ({
      save: () => {
        return JSON.stringify(editor ? editor.getJSON() : '');
      },
      clear: () => {
        if (editor) {
          editor.commands.clearContent();
        }
      },
    }));

    const buttons = [
      {
        id: 'bold',
        icon: <TextBolderIcon />,
        onClick: () => {
          editor?.chain().focus().toggleBold().run();
        },
      },
      {
        id: 'italic',
        icon: <TextItalicIcon />,
        onClick: () => {
          editor?.chain().focus().toggleItalic().run();
        },
      },
      {
        id: 'underline',
        icon: <TextUnderlineIcon />,
        onClick: () => {
          editor?.chain().focus().toggleUnderline().run();
        },
      },
      {
        id: 'strikethrough',
        icon: <TextStrikethroughIcon />,
        onClick: () => {
          editor?.chain().focus().toggleStrike().run();
        },
      },
      {
        id: 'code',
        icon: <CodeIcon />,
        onClick: () => {
          editor?.chain().focus().toggleCodeBlock().run();
        },
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
