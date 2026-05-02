'use client';

import { useTranslations } from 'next-intl';
import { useParams } from 'next/navigation';
import { useRef, useState } from 'react';

import { useGetProfileByHandle } from '@/api/__generated__/profile/profile';
import { useCreateReflection } from '@/api/__generated__/reflection/reflection';
import { Button } from '@/components/ui/button';
import {
  Dialog,
  DialogClose,
  DialogContent,
  DialogDescription,
  DialogFooter,
  DialogHeader,
  DialogTitle,
  DialogTrigger,
} from '@/components/ui/dialog';
import { BaseEditor, type BaseEditorRef } from '@/components/ui/editor/base';
import { ScrollArea } from '@/components/ui/scroll-area';

export default function CreateReflectionDialog() {
  const t = useTranslations('pages.profile.posts');

  const { handle } = useParams();

  const editorRef = useRef<BaseEditorRef | null>(null);

  const [dialogOpen, setDialogOpen] = useState(false);

  const { data: profile } = useGetProfileByHandle(handle?.toString() || '');
  const { mutate: createReflection } = useCreateReflection();

  const handleCancel = () => {
    editorRef.current?.clear();
    setDialogOpen(false);
  };

  const handleSave = () => {
    if (!editorRef.current) {
      return;
    }

    const content = editorRef.current.save();
    createReflection(
      {
        data: {
          content,
        },
      },
      {
        onSuccess: () => {
          editorRef.current?.clear();
          setDialogOpen(false);
        },
      },
    );
  };

  if (!profile?.data.isAuthenticatedUser) {
    return null;
  }

  return (
    <Dialog open={dialogOpen} onOpenChange={setDialogOpen}>
      <DialogTrigger asChild>
        <Button>{t('write.label')}</Button>
      </DialogTrigger>
      <DialogContent>
        <DialogHeader>
          <div className="flex items-center justify-between">
            <div className="space-y-1">
              <DialogTitle>{t('write.title')}</DialogTitle>
              <DialogDescription>{t('write.description')}</DialogDescription>
            </div>

            <DialogClose />
          </div>
        </DialogHeader>

        <ScrollArea className="h-[300px] w-full rounded-md border">
          <BaseEditor ref={editorRef} />
        </ScrollArea>

        <DialogFooter>
          <Button variant="destructive" onClick={handleCancel}>
            {t('write.actions.cancel')}
          </Button>
          <Button onClick={handleSave}>{t('write.actions.submit')}</Button>
        </DialogFooter>
      </DialogContent>
    </Dialog>
  );
}
