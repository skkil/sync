'use client';

import { ImageIcon, XIcon } from '@phosphor-icons/react';
import { Placeholder } from '@tiptap/extensions';
import { EditorContent, useEditor } from '@tiptap/react';
import StarterKit from '@tiptap/starter-kit';
import { useTranslations } from 'next-intl';
import NextImage from 'next/image';
import Link from 'next/link';
import { useRouter } from 'next/navigation';
import { type ChangeEvent, useEffect, useRef, useState } from 'react';
import { toast } from 'sonner';

import { useUploadMedia } from '@/api/__generated__/media/media';
import { useCreateReflection } from '@/api/__generated__/reflection/reflection';
import { uploadFileToS3 } from '@/api/s3';
import { Avatar, AvatarFallback, AvatarImage } from '@/components/ui/avatar';
import { Button } from '@/components/ui/button';
import { ScrollArea } from '@/components/ui/scroll-area';
import { Skeleton } from '@/components/ui/skeleton';
import { useSession } from '@/lib/auth/client';
import { cn } from '@/lib/utils';

const MAX_IMAGE_COUNT = 4;

interface SelectedImage {
  id: string;
  file: File;
  previewUrl: string;
  mediaId?: number;
  isUploading?: boolean;
  uploadError?: boolean;
}

interface TiptapContent {
  type: string;
  attrs?: Record<string, unknown>;
  content?: TiptapContent[];
  marks?: unknown[];
  text?: string;
}

function appendImagesToContent(
  content: TiptapContent,
  images: SelectedImage[],
): TiptapContent {
  if (images.length === 0) {
    return content;
  }

  const imageNodes = images
    .filter((image) => image.mediaId)
    .map((image) => ({
      type: 'image',
      attrs: {
        alt: image.file.name,
        mediaId: image.mediaId,
        src: '',
      },
    }));

  return {
    ...content,
    content: [...(content.content ?? []), ...imageNodes],
  };
}

export default function MiniPostEditor() {
  const t = useTranslations('pages.home.post-editor');
  const router = useRouter();
  const imageInputRef = useRef<HTMLInputElement>(null);
  const selectedImagesRef = useRef<SelectedImage[]>([]);

  const { data: session, isPending: isSessionPending } = useSession();

  const { mutate: createPost, isPending: isCreatingPost } =
    useCreateReflection();
  const { mutateAsync: uploadMedia } = useUploadMedia();

  const [isFocused, setIsFocused] = useState(false);
  const [isEmpty, setIsEmpty] = useState(true);
  const [selectedImages, setSelectedImages] = useState<SelectedImage[]>([]);
  const [isUploadingImages, setIsUploadingImages] = useState(false);

  const editor = useEditor({
    extensions: [
      StarterKit,
      Placeholder.configure({
        placeholder: t('placeholder'),
        showOnlyWhenEditable: true,
      }),
    ],
    content: '',
    onUpdate: ({ editor }) => {
      if (!editor) {
        setIsEmpty(true);
        return;
      }

      setIsEmpty(editor.isEmpty);
    },
    immediatelyRender: false,
    editorProps: {
      attributes: {
        class: 'focus:outline-none focus:ring-0',
      },
    },
  });

  useEffect(() => {
    selectedImagesRef.current = selectedImages;
  }, [selectedImages]);

  useEffect(() => {
    return () => {
      selectedImagesRef.current.forEach((image) =>
        URL.revokeObjectURL(image.previewUrl),
      );
    };
  }, []);

  if (isSessionPending) {
    return <Skeleton className="h-24" />;
  }

  if (!session) {
    return null;
  }

  const handleImageChange = (event: ChangeEvent<HTMLInputElement>) => {
    const files = Array.from(event.target.files ?? []).filter((file) =>
      file.type.startsWith('image/'),
    );

    setSelectedImages((currentImages) => {
      const availableCount = MAX_IMAGE_COUNT - currentImages.length;

      if (availableCount <= 0) {
        return currentImages;
      }

      const images = files.slice(0, availableCount).map((file) => {
        const previewUrl = URL.createObjectURL(file);

        return {
          id: previewUrl,
          file,
          previewUrl,
        };
      });

      return [...currentImages, ...images];
    });

    event.target.value = '';
  };

  const handleRemoveImage = (imageId: string) => {
    setSelectedImages((currentImages) => {
      const image = currentImages.find(({ id }) => id === imageId);
      if (image) {
        URL.revokeObjectURL(image.previewUrl);
      }

      return currentImages.filter(({ id }) => id !== imageId);
    });
  };

  const updateSelectedImage = (
    imageId: string,
    updates: Partial<SelectedImage>,
  ) => {
    setSelectedImages((currentImages) =>
      currentImages.map((image) =>
        image.id === imageId ? { ...image, ...updates } : image,
      ),
    );
  };

  const uploadSelectedImages = async () => {
    const uploadedImages: SelectedImage[] = [];

    for (const image of selectedImages) {
      if (image.mediaId) {
        uploadedImages.push(image);
        continue;
      }

      updateSelectedImage(image.id, {
        isUploading: true,
        uploadError: false,
      });

      try {
        const {
          data: { uploadUrl, mediaId },
        } = await uploadMedia({
          data: {
            fileName: image.file.name,
            fileSize: image.file.size,
            mediaType: image.file.type,
          },
        });

        const { success } = await uploadFileToS3({
          file: image.file,
          uploadUrl,
        });

        if (!success) {
          throw new Error('Failed to upload image');
        }

        const uploadedImage = {
          ...image,
          mediaId,
          isUploading: false,
          uploadError: false,
        };

        updateSelectedImage(image.id, uploadedImage);
        uploadedImages.push(uploadedImage);
      } catch (error) {
        updateSelectedImage(image.id, {
          isUploading: false,
          uploadError: true,
        });

        throw error;
      }
    }

    return uploadedImages;
  };

  const handleSubmit = async () => {
    if (!editor || editor.isEmpty) {
      return;
    }

    let uploadedImages: SelectedImage[] = [];

    if (selectedImages.length > 0) {
      setIsUploadingImages(true);

      try {
        uploadedImages = await uploadSelectedImages();
      } catch {
        toast.error('Failed to upload image.');
        setIsUploadingImages(false);
        return;
      } finally {
        setIsUploadingImages(false);
      }
    }

    const contentJson = appendImagesToContent(
      editor.getJSON() as TiptapContent,
      uploadedImages,
    );

    createPost(
      {
        data: {
          content: {
            text: editor.getText(),
            json: JSON.stringify(contentJson),
          },
        },
      },
      {
        onSuccess: ({ data: { slug } }) => {
          router.push(`/posts/${slug}`);
        },
      },
    );
  };

  const isPostDisabled = isEmpty || isCreatingPost || isUploadingImages;

  return (
    <div className="w-full mx-auto">
      <div className="flex gap-3">
        <Avatar>
          <AvatarImage src={session.user.image ?? undefined} />
          <AvatarFallback>{session.user.name[0]}</AvatarFallback>
        </Avatar>

        <div className="flex-1 space-y-3">
          <div
            className={cn(
              'rounded-lg border transition-all',
              isFocused || selectedImages.length > 0
                ? 'border-primary h-64'
                : 'border-border h-32',
            )}
            onClick={() => editor?.commands.focus()}
          >
            <ScrollArea className="h-full w-full">
              <div className="space-y-3 px-3 py-2">
                <EditorContent
                  editor={editor}
                  onFocus={() => setIsFocused(true)}
                  onBlur={() => setIsFocused(false)}
                  className="w-full max-w-full"
                />

                {selectedImages.length > 0 && (
                  <div
                    className={cn(
                      'grid gap-2 overflow-hidden rounded-lg',
                      selectedImages.length === 1
                        ? 'grid-cols-1'
                        : 'grid-cols-2',
                    )}
                  >
                    {selectedImages.map((image) => (
                      <div
                        key={image.id}
                        className={cn(
                          'relative overflow-hidden rounded-lg border',
                          selectedImages.length === 1
                            ? 'bg-background'
                            : 'aspect-video bg-muted',
                          image.uploadError && 'border-destructive',
                          image.isUploading && 'opacity-60',
                        )}
                      >
                        {selectedImages.length === 1 ? (
                          // eslint-disable-next-line @next/next/no-img-element
                          <img
                            src={image.previewUrl}
                            alt={image.file.name}
                            className="block w-full"
                          />
                        ) : (
                          <NextImage
                            src={image.previewUrl}
                            alt={image.file.name}
                            fill
                            unoptimized
                            sizes="(max-width: 768px) 100vw, 320px"
                            className="h-full w-full object-cover"
                          />
                        )}
                        <Button
                          type="button"
                          variant="secondary"
                          size="icon-xs"
                          className="absolute top-2 right-2 bg-background/80 hover:bg-background"
                          aria-label="Remove image"
                          disabled={image.isUploading}
                          onClick={() => handleRemoveImage(image.id)}
                        >
                          <XIcon />
                        </Button>
                      </div>
                    ))}
                  </div>
                )}
              </div>
            </ScrollArea>
          </div>

          <div className="flex items-center justify-between">
            <div className="flex items-center gap-2">
              <Button
                type="button"
                variant="ghost"
                size="icon-sm"
                aria-label="Add images"
                disabled={
                  isUploadingImages || selectedImages.length >= MAX_IMAGE_COUNT
                }
                onClick={() => imageInputRef.current?.click()}
              >
                <ImageIcon />
              </Button>
              <input
                ref={imageInputRef}
                type="file"
                accept="image/*"
                multiple
                className="hidden"
                onChange={handleImageChange}
              />
            </div>
            <div className="flex items-center gap-3">
              <Button asChild>
                <Link href="/posts/new">{t('post.advanced')}</Link>
              </Button>

              <Button
                disabled={isPostDisabled}
                isPending={isCreatingPost || isUploadingImages}
                onClick={handleSubmit}
              >
                <span>{t('post.label')}</span>
              </Button>
            </div>
          </div>
        </div>
      </div>
    </div>
  );
}
