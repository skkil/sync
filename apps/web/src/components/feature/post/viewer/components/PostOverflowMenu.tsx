'use client';

import {
  CopyIcon,
  DotsThreeIcon,
  SirenIcon,
  TrashIcon,
} from '@phosphor-icons/react';
import { useQueryClient } from '@tanstack/react-query';
import { useTranslations } from 'next-intl';
import { useRouter } from 'next/navigation';
import { useState } from 'react';
import { toast } from 'sonner';

import {
  getGetPostBySlugQueryKey,
  useDeletePost,
} from '@/api/__generated__/post/post';
import {
  AlertDialog,
  AlertDialogAction,
  AlertDialogCancel,
  AlertDialogContent,
  AlertDialogDescription,
  AlertDialogFooter,
  AlertDialogHeader,
  AlertDialogTitle,
} from '@/components/ui/alert-dialog';
import { Button } from '@/components/ui/button';
import {
  DropdownMenu,
  DropdownMenuContent,
  DropdownMenuItem,
  DropdownMenuTrigger,
} from '@/components/ui/dropdown-menu';
import { useSession } from '@/lib/auth/client';
import ROUTES from '@/util/routes';

import { ReportPostDialog } from './ReportPostDialog';

interface PostOverflowMenuProps {
  postId: number;
  slug: string;
  authorHandle: string;
  projectHandle?: string;
  showReport?: boolean;
  redirectAfterDelete?: string;
  onDeleted?: () => void;
}

export function PostOverflowMenu({
  postId,
  slug,
  authorHandle,
  projectHandle,
  showReport = true,
  redirectAfterDelete,
  onDeleted,
}: PostOverflowMenuProps) {
  const t = useTranslations('components.post.actions');
  const router = useRouter();
  const queryClient = useQueryClient();
  const { data: session } = useSession();
  const { mutate: deletePost, isPending: isDeleting } = useDeletePost();
  const [reportOpen, setReportOpen] = useState(false);
  const [deleteOpen, setDeleteOpen] = useState(false);

  const isAuthor = session?.user.handle === authorHandle;
  const postPath = projectHandle
    ? ROUTES.PROJECT_POST(projectHandle, slug)
    : ROUTES.POST(slug);

  const copyPostLink = async () => {
    const url = new URL(postPath, window.location.origin).toString();

    try {
      await writeToClipboard(url);
      toast.success(t('copy-success'));
    } catch {
      toast.error(t('copy-error'));
    }
  };

  const confirmDelete = () => {
    deletePost(
      { postId: String(postId) },
      {
        onSuccess: async () => {
          toast.success(t('delete-success'));
          setDeleteOpen(false);
          onDeleted?.();

          await invalidatePostQueries(queryClient, slug);

          if (redirectAfterDelete) {
            router.replace(redirectAfterDelete);
            return;
          }

          router.refresh();
        },
        onError: () => {
          toast.error(t('delete-error'));
        },
      },
    );
  };

  return (
    <>
      <div
        onClick={(event) => event.stopPropagation()}
        onPointerDown={(event) => event.stopPropagation()}
      >
        <DropdownMenu>
          <DropdownMenuTrigger asChild>
            <Button variant="ghost" size="icon-sm" aria-label={t('menu')}>
              <DotsThreeIcon weight="bold" />
            </Button>
          </DropdownMenuTrigger>

          <DropdownMenuContent align="end">
            <DropdownMenuItem
              onSelect={() => {
                void copyPostLink();
              }}
            >
              <CopyIcon />
              {t('copy-link')}
            </DropdownMenuItem>

            {showReport && !isAuthor && (
              <DropdownMenuItem
                variant="destructive"
                onSelect={() => {
                  setReportOpen(true);
                }}
              >
                <SirenIcon />
                {t('report')}
              </DropdownMenuItem>
            )}

            {isAuthor && (
              <DropdownMenuItem
                variant="destructive"
                disabled={isDeleting}
                onSelect={() => {
                  setDeleteOpen(true);
                }}
              >
                <TrashIcon />
                {t('delete')}
              </DropdownMenuItem>
            )}
          </DropdownMenuContent>
        </DropdownMenu>
      </div>

      <ReportPostDialog
        postId={postId}
        open={reportOpen}
        onOpenChange={setReportOpen}
      />

      <AlertDialog open={deleteOpen} onOpenChange={setDeleteOpen}>
        <AlertDialogContent onClick={(event) => event.stopPropagation()}>
          <AlertDialogHeader>
            <AlertDialogTitle>{t('delete-title')}</AlertDialogTitle>
            <AlertDialogDescription>
              {t('delete-description')}
            </AlertDialogDescription>
          </AlertDialogHeader>

          <AlertDialogFooter>
            <AlertDialogCancel disabled={isDeleting}>
              {t('cancel')}
            </AlertDialogCancel>
            <AlertDialogAction
              variant="destructive"
              disabled={isDeleting}
              onClick={(event) => {
                event.preventDefault();
                confirmDelete();
              }}
            >
              {isDeleting ? t('deleting') : t('delete')}
            </AlertDialogAction>
          </AlertDialogFooter>
        </AlertDialogContent>
      </AlertDialog>
    </>
  );
}

async function writeToClipboard(text: string) {
  if (navigator.clipboard?.writeText) {
    await navigator.clipboard.writeText(text);
    return;
  }

  const textarea = document.createElement('textarea');
  textarea.value = text;
  textarea.setAttribute('readonly', '');
  textarea.style.position = 'fixed';
  textarea.style.opacity = '0';
  document.body.appendChild(textarea);
  textarea.select();

  try {
    const copied = document.execCommand('copy');
    if (!copied) {
      throw new Error('copy failed');
    }
  } finally {
    document.body.removeChild(textarea);
  }
}

async function invalidatePostQueries(
  queryClient: ReturnType<typeof useQueryClient>,
  slug: string,
) {
  await Promise.all([
    queryClient.invalidateQueries({
      queryKey: getGetPostBySlugQueryKey(slug),
    }),
    queryClient.invalidateQueries({
      predicate: (query) => {
        const path = getQueryPath(query.queryKey);

        return isPostListQueryPath(path);
      },
    }),
  ]);
}

function getQueryPath(queryKey: readonly unknown[]) {
  const path = queryKey[0] === 'infinite' ? queryKey[1] : queryKey[0];

  return typeof path === 'string' ? path : '';
}

function isPostListQueryPath(path: string) {
  return (
    path === '/feed/recent' ||
    path === '/search/posts' ||
    path === '/bookmarks/posts' ||
    path === '/posts' ||
    /^\/users\/[^/]+\/posts$/.test(path) ||
    /^\/projects\/[^/]+\/posts$/.test(path) ||
    /^\/profiles\/[^/]+\/posts\/activities$/.test(path)
  );
}
