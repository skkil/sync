import { useTranslations } from 'next-intl';
import { useRouter } from 'next/navigation';
import { useState } from 'react';
import { toast } from 'sonner';

import { useDeletePost } from '@/components/feature/post/hooks/useDeletePost';
import ROUTES from '@/util/routes';

export function useDeletePostDialog(
  postId: number,
  { redirectOnSuccess }: { redirectOnSuccess: boolean },
) {
  const tDelete = useTranslations('pages.posts.delete');
  const router = useRouter();
  const [isOpen, setIsOpen] = useState(false);
  const { mutate: deletePost, isPending } = useDeletePost();

  const confirmDelete = () => {
    deletePost(
      { postId: String(postId) },
      {
        onSuccess: () => {
          toast.success(tDelete('messages.success'));
          setIsOpen(false);

          if (redirectOnSuccess) {
            router.push(ROUTES.HOME());
          }
        },
        onError: () => {
          toast.error(tDelete('messages.error'));
        },
      },
    );
  };

  return {
    isOpen,
    open: () => setIsOpen(true),
    close: () => setIsOpen(false),
    confirmDelete,
    isPending,
  };
}
