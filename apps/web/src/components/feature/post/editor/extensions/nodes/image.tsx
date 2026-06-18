import { ArrowCounterClockwiseIcon, ImageIcon } from '@phosphor-icons/react';
import {
  Node,
  NodeViewProps,
  NodeViewWrapper,
  ReactNodeViewRenderer,
  mergeAttributes,
} from '@tiptap/react';
import Image from 'next/image';
import { useEffect, useRef } from 'react';
import { toast } from 'sonner';

import { useUploadMedia } from '@/api/__generated__/media/media';
import { uploadFileToS3 } from '@/api/s3';
import { Button } from '@/components/ui/button';
import { FileInput } from '@/components/ui/input';
import { Spinner } from '@/components/ui/spinner';
import { cn } from '@/lib/tiptap-utils';

import { NodeType } from '.';

type ImageNodeAttributes = {
  src: string | null;
  status: 'none' | 'loaded' | 'uploading' | 'uploaded' | 'error';
  mediaId: string | null;
};

export const ImageNode = Node.create<ImageNodeAttributes>({
  name: NodeType.Image,
  group: 'block',
  content: '',
  atom: true,
  selectable: true,
  draggable: true,
  addAttributes() {
    return {
      src: {
        default: null,
      },
      status: {
        default: 'none',
        rendered: false,
      },
      mediaId: {
        default: null,
        rendered: false,
      },
    };
  },
  parseHTML() {
    return [
      {
        tag: 'img[src]',
        getAttrs: (element) => ({
          status: element.getAttribute('src') ? 'uploaded' : 'none',
        }),
      },
    ];
  },
  renderHTML({ HTMLAttributes }) {
    return ['img', mergeAttributes(HTMLAttributes)];
  },
  addNodeView() {
    return ReactNodeViewRenderer(ImageNodeComponent);
  },
});

function ImageNodeComponent({
  node,
  selected,
  updateAttributes,
}: NodeViewProps) {
  const { src, status } = node.attrs as ImageNodeAttributes;

  const { mutateAsync: uploadImage } = useUploadMedia();

  const objectURLRef = useRef<string | null>(null);

  useEffect(() => {
    return () => {
      if (objectURLRef.current) {
        URL.revokeObjectURL(objectURLRef.current);
      }
    };
  }, []);

  const handleFileUpload = (files: File[]) => {
    const file = files[0];
    if (!file) {
      return;
    }

    const objectUrl = URL.createObjectURL(file);
    objectURLRef.current = objectUrl;

    updateAttributes({
      src: objectUrl,
      status: 'uploading',
    });

    uploadImage({
      data: {
        fileName: file.name,
        fileSize: file.size,
        mediaType: file.type,
      },
    })
      .then(({ data: { mediaId, uploadUrl } }) => {
        updateAttributes({
          mediaId,
        });

        return uploadFileToS3({
          uploadUrl,
          file,
        });
      })
      .then(({ success }) => {
        if (!success) {
          throw new Error('Failed to upload file to S3');
        }

        updateAttributes({
          status: 'uploaded',
        });
        toast.success('Image uploaded successfully');
      })
      .catch((error) => {
        toast.error(
          error instanceof Error ? error.message : 'Failed to upload image',
        );

        updateAttributes({
          status: 'error',
        });
      });
  };

  return (
    <NodeViewWrapper>
      <div
        className={cn(
          'w-full rounded-lg overflow-hidden',
          selected && 'border-l',
        )}
      >
        {status === 'none' ? (
          <FileInput
            accept="image/*"
            onFileChange={handleFileUpload}
            maxFiles={1}
          >
            <div className="flex w-full flex-col items-center justify-center gap-2 bg-muted py-10 text-muted-foreground transition-colors hover:border-primary/50 hover:bg-muted/50 hover:text-foreground">
              <ImageIcon className="size-8" />
              <span className="text-sm font-medium">
                Click to upload an image
              </span>
            </div>
          </FileInput>
        ) : (
          <div className="relative aspect-video w-full">
            {src && (
              <Image
                src={src}
                alt="Uploaded image"
                fill
                className={cn(
                  'object-cover',
                  status === 'uploading' && 'blur-sm brightness-75',
                  status === 'error' && 'opacity-25',
                )}
              />
            )}

            {status === 'uploading' && (
              <div className="absolute inset-0 flex items-center justify-center">
                <Spinner className="size-6 border-white border-t-transparent" />
              </div>
            )}

            {status === 'error' && (
              <div className="absolute inset-0 flex items-center justify-center">
                <div className="flex w-full flex-col items-center justify-center gap-3 rounded-lg py-10">
                  <span className="text-xl text-destructive">
                    Failed to upload image
                  </span>

                  <FileInput
                    accept="image/*"
                    onFileChange={handleFileUpload}
                    maxFiles={1}
                  >
                    <Button variant="outline" size="sm">
                      <ArrowCounterClockwiseIcon className="size-4" />
                      Try again
                    </Button>
                  </FileInput>
                </div>
              </div>
            )}
          </div>
        )}
      </div>
    </NodeViewWrapper>
  );
}
