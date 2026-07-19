'use client';

import {
  DotsThreeIcon,
  PencilSimpleIcon,
  SirenIcon,
} from '@phosphor-icons/react';
import { useTranslations } from 'next-intl';
import { useRouter } from 'next/navigation';
import { toast } from 'sonner';

import { ProfileHoverCard } from '@/components/feature/profile/ProfileHoverCard';
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
import {
  DropdownMenu,
  DropdownMenuContent,
  DropdownMenuItem,
  DropdownMenuTrigger,
} from '@/components/ui/dropdown-menu';
import { RelativeTime } from '@/components/ui/relative-time';
import ROUTES from '@/util/routes';

import { useDeletePostDialog } from '../hooks/useDeletePostDialog';
import { useReportPostDialog } from '../hooks/useReportPostDialog';
import type { PostCardVariant, PostSummary } from '../types';
import { ReportPostDialog } from './ReportPostDialog';

export function PostViewHeader({
  summary,
  postPath,
  variant,
}: {
  summary: PostSummary;
  postPath: string;
  variant: PostCardVariant;
}) {
  const t = useTranslations('pages.posts.report');
  const tDelete = useTranslations('pages.posts.delete');
  const tCopyLink = useTranslations('pages.posts.copy-link');
  const tViewer = useTranslations('components.post.viewer');
  const tEdit = useTranslations('pages.posts.edit');
  const router = useRouter();

  const isPreview = variant === 'preview';
  const report = useReportPostDialog();
  const deleteDialog = useDeletePostDialog(summary.id, {
    redirectTo: isPreview ? undefined : ROUTES.HOME(),
  });

  const handleCopyLink = async () => {
    try {
      await navigator.clipboard.writeText(window.location.origin + postPath);
      toast.success(tCopyLink('messages.success'));
    } catch {
      toast.error(tCopyLink('messages.error'));
    }
  };

  const stopPropagation = isPreview
    ? (event: React.MouseEvent) => event.stopPropagation()
    : undefined;

  return (
    <>
      <div className="flex items-start justify-between">
        <div className="flex items-center gap-2">
          <div onClick={stopPropagation}>
            <ProfileHoverCard
              handle={summary.author.handle}
              name={summary.author.name}
              size={isPreview ? 'default' : 'sm'}
            />
          </div>

          <div className="flex flex-col">
            <span className="text-sm font-semibold">{summary.author.name}</span>
            <span className="text-muted-foreground text-xs">
              @{summary.author.handle} ·{' '}
              <RelativeTime date={summary.createdAt} />
            </span>
          </div>
        </div>

        <DropdownMenu>
          <DropdownMenuTrigger asChild>
            <Button
              variant="ghost"
              size="icon-sm"
              aria-label={tViewer('options')}
              onClick={stopPropagation}
            >
              <DotsThreeIcon weight="bold" />
            </Button>
          </DropdownMenuTrigger>

          <DropdownMenuContent align="end" onClick={stopPropagation}>
            <DropdownMenuItem onSelect={handleCopyLink}>
              {tCopyLink('trigger')}
            </DropdownMenuItem>
            {summary.isAuthor ? (
              <>
                <DropdownMenuItem
                  onSelect={() =>
                    router.push(
                      summary.project?.handle
                        ? ROUTES.PROJECT_POST_EDIT(
                            summary.project.handle,
                            summary.slug,
                          )
                        : ROUTES.POST_EDIT(summary.slug),
                    )
                  }
                >
                  <PencilSimpleIcon />
                  {tEdit('trigger')}
                </DropdownMenuItem>
                <DropdownMenuItem
                  variant="destructive"
                  onSelect={() => deleteDialog.open()}
                >
                  {tDelete('trigger')}
                </DropdownMenuItem>
              </>
            ) : isPreview ? (
              <DropdownMenuItem variant="destructive">
                {t('trigger')}
              </DropdownMenuItem>
            ) : (
              <DropdownMenuItem
                variant="destructive"
                onSelect={() => report.open()}
              >
                <SirenIcon />
                {t('trigger')}
              </DropdownMenuItem>
            )}
          </DropdownMenuContent>
        </DropdownMenu>
      </div>

      {!isPreview && (
        <div onClick={stopPropagation}>
          <ReportPostDialog
            postId={summary.id}
            open={report.isOpen}
            onOpenChange={(open) => (open ? report.open() : report.close())}
          />
        </div>
      )}

      <AlertDialog
        open={deleteDialog.isOpen}
        onOpenChange={(open) =>
          open ? deleteDialog.open() : deleteDialog.close()
        }
      >
        <AlertDialogContent onClick={stopPropagation}>
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
