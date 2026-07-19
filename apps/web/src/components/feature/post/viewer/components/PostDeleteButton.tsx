'use client';

import { TrashIcon } from '@phosphor-icons/react';
import { useTranslations } from 'next-intl';

import {
  AlertDialog,
  AlertDialogAction,
  AlertDialogCancel,
  AlertDialogContent,
  AlertDialogFooter,
  AlertDialogHeader,
  AlertDialogTitle,
} from '@/components/ui/alert-dialog';
import { Button } from '@/components/ui/button';

import { useDeletePostDialog } from '../hooks/useDeletePostDialog';

interface PostDeleteButtonProps {
  postId: number;
  redirectTo?: string;
  className?: string;
  size?: 'default' | 'sm';
}

export function PostDeleteButton({
  postId,
  redirectTo,
  className,
  size = 'default',
}: PostDeleteButtonProps) {
  const tDelete = useTranslations('pages.posts.delete');
  const deleteDialog = useDeletePostDialog(postId, { redirectTo });

  return (
    <>
      <Button
        variant="destructive"
        size={size}
        className={className}
        onClick={deleteDialog.open}
      >
        <TrashIcon />
        {tDelete('trigger')}
      </Button>

      <AlertDialog
        open={deleteDialog.isOpen}
        onOpenChange={(open) =>
          open ? deleteDialog.open() : deleteDialog.close()
        }
      >
        <AlertDialogContent>
          <AlertDialogHeader>
            <AlertDialogTitle>{tDelete('title')}</AlertDialogTitle>
          </AlertDialogHeader>

          <AlertDialogFooter>
            <AlertDialogCancel>{tDelete('actions.cancel')}</AlertDialogCancel>
            <AlertDialogAction
              variant="destructive"
              disabled={deleteDialog.isPending}
              onClick={deleteDialog.confirmDelete}
            >
              {tDelete('actions.confirm')}
            </AlertDialogAction>
          </AlertDialogFooter>
        </AlertDialogContent>
      </AlertDialog>
    </>
  );
}
