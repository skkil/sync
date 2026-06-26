'use client';

import {
  CheckCircleIcon,
  EyeSlashIcon,
  FunnelIcon,
} from '@phosphor-icons/react';
import { useQueryClient } from '@tanstack/react-query';
import { EditorContent, JSONContent, useEditor } from '@tiptap/react';
import StarterKit from '@tiptap/starter-kit';
import { useTranslations } from 'next-intl';
import Link from 'next/link';
import { useState } from 'react';
import { toast } from 'sonner';

import {
  getGetPostReportsQueryKey,
  useGetPostReports,
  useReviewPostReport,
} from '@/api/__generated__/post-report/post-report';
import type { GetPostReportsResponseReportsContentItem } from '@/api/__generated__/types/GetPostReportsResponseReportsContentItem';
import { ReviewPostReportRequestResolution } from '@/api/__generated__/types/ReviewPostReportRequestResolution';
import { ImageNode } from '@/components/feature/post/editor/extensions/nodes/image';
import { deserialize } from '@/components/feature/post/editor/utils/serializer';
import { Badge } from '@/components/ui/badge';
import { Button } from '@/components/ui/button';
import {
  Dialog,
  DialogContent,
  DialogFooter,
  DialogHeader,
  DialogTitle,
} from '@/components/ui/dialog';
import { Label } from '@/components/ui/label';
import {
  Select,
  SelectContent,
  SelectItem,
  SelectTrigger,
  SelectValue,
} from '@/components/ui/select';
import { Skeleton } from '@/components/ui/skeleton';
import {
  Table,
  TableBody,
  TableCell,
  TableHead,
  TableHeader,
  TableRow,
} from '@/components/ui/table';
import { Textarea } from '@/components/ui/textarea';

const PAGE_SIZE = '20';
const REPORT_STATUSES = ['PENDING', 'RESOLVED', 'DISMISSED'] as const;

type ReportStatus = (typeof REPORT_STATUSES)[number];

export default function AdminPostReportsPage() {
  const t = useTranslations('pages.admin.post-reports');
  const queryClient = useQueryClient();
  const [status, setStatus] = useState<ReportStatus>('PENDING');
  const [page, setPage] = useState(0);
  const [selectedReport, setSelectedReport] =
    useState<GetPostReportsResponseReportsContentItem | null>(null);
  const [resolutionNote, setResolutionNote] = useState('');
  const [hiddenReason, setHiddenReason] = useState('');

  const params = { status, page: String(page), size: PAGE_SIZE };
  const { data, isPending } = useGetPostReports(params);
  const { mutate: reviewPostReport, isPending: isReviewPending } =
    useReviewPostReport();

  const reports = data?.data.reports?.content ?? [];
  const pageInfo = data?.data.reports?.pageInfo;

  const invalidateReports = async () => {
    await queryClient.invalidateQueries({
      queryKey: getGetPostReportsQueryKey(params),
    });
  };

  const dismissReport = (report: GetPostReportsResponseReportsContentItem) => {
    reviewPostReport(
      {
        reportId: String(report.id),
        data: {
          resolution: ReviewPostReportRequestResolution.Dismiss,
          resolutionNote: t('actions.dismiss-note'),
          hiddenReason: null,
        },
      },
      {
        onSuccess: async () => {
          toast.success(t('messages.dismiss-success'));
          await invalidateReports();
        },
        onError: () => {
          toast.error(t('messages.review-error'));
        },
      },
    );
  };

  const hidePost = () => {
    if (!selectedReport) {
      return;
    }

    reviewPostReport(
      {
        reportId: String(selectedReport.id),
        data: {
          resolution: ReviewPostReportRequestResolution.HidePost,
          resolutionNote: resolutionNote.trim() || null,
          hiddenReason: hiddenReason.trim() || resolutionNote.trim() || null,
        },
      },
      {
        onSuccess: async () => {
          toast.success(t('messages.hide-success'));
          setSelectedReport(null);
          setResolutionNote('');
          setHiddenReason('');
          await invalidateReports();
        },
        onError: () => {
          toast.error(t('messages.review-error'));
        },
      },
    );
  };

  return (
    <div className="mx-auto flex w-full max-w-6xl flex-col gap-5">
      <div className="flex flex-col gap-3 sm:flex-row sm:items-center sm:justify-between">
        <h1 className="text-2xl font-semibold">{t('title')}</h1>
        <div className="flex items-center gap-2">
          <FunnelIcon className="text-muted-foreground" />
          <Select
            value={status}
            onValueChange={(value) => {
              setStatus(value as ReportStatus);
              setPage(0);
            }}
          >
            <SelectTrigger>
              <SelectValue />
            </SelectTrigger>
            <SelectContent>
              {REPORT_STATUSES.map((value) => (
                <SelectItem key={value} value={value}>
                  {t(`statuses.${value}`)}
                </SelectItem>
              ))}
            </SelectContent>
          </Select>
        </div>
      </div>

      {isPending ? (
        <Skeleton className="h-72 w-full" />
      ) : (
        <Table>
          <TableHeader>
            <TableRow>
              <TableHead>{t('table.post')}</TableHead>
              <TableHead>{t('table.reporter')}</TableHead>
              <TableHead>{t('table.reason')}</TableHead>
              <TableHead>{t('table.status')}</TableHead>
              <TableHead>{t('table.createdAt')}</TableHead>
              <TableHead>{t('table.actions')}</TableHead>
            </TableRow>
          </TableHeader>
          <TableBody>
            {reports.length === 0 ? (
              <TableRow>
                <TableCell colSpan={6} className="h-24 text-center">
                  {t('empty')}
                </TableCell>
              </TableRow>
            ) : (
              reports.map((report) => (
                <TableRow key={report.id}>
                  <TableCell className="min-w-72 max-w-96 whitespace-normal">
                    <div className="space-y-2">
                      {report.post && (
                        <Link
                          href={`/posts/${report.post.slug}`}
                          className="font-medium hover:underline"
                        >
                          {report.post.title || report.post.slug}
                        </Link>
                      )}
                      {report.post?.content && (
                        <div className="line-clamp-3 text-muted-foreground">
                          <PostContentPreview content={report.post.content} />
                        </div>
                      )}
                    </div>
                  </TableCell>
                  <TableCell>
                    <div className="flex flex-col">
                      <span>{report.reporter?.name ?? '-'}</span>
                      <span className="text-xs text-muted-foreground">
                        @{report.reporter?.handle ?? '-'}
                      </span>
                    </div>
                  </TableCell>
                  <TableCell>
                    <div className="flex flex-col gap-1">
                      <span>{t(`reasons.${report.reason}`)}</span>
                      {report.description && (
                        <span className="max-w-56 whitespace-normal text-xs text-muted-foreground">
                          {report.description}
                        </span>
                      )}
                    </div>
                  </TableCell>
                  <TableCell>
                    <Badge variant="outline">
                      {t(`statuses.${report.status}`)}
                    </Badge>
                  </TableCell>
                  <TableCell>{formatDate(report.createdAt)}</TableCell>
                  <TableCell>
                    {report.status === 'PENDING' ? (
                      <div className="flex gap-2">
                        <Button
                          size="sm"
                          variant="outline"
                          onClick={() => dismissReport(report)}
                        >
                          <CheckCircleIcon />
                          {t('actions.dismiss')}
                        </Button>
                        <Button
                          size="sm"
                          variant="destructive"
                          onClick={() => setSelectedReport(report)}
                        >
                          <EyeSlashIcon />
                          {t('actions.hide')}
                        </Button>
                      </div>
                    ) : (
                      <span className="text-muted-foreground">
                        {report.resolutionNote || '-'}
                      </span>
                    )}
                  </TableCell>
                </TableRow>
              ))
            )}
          </TableBody>
        </Table>
      )}

      {pageInfo && (
        <div className="flex items-center justify-end gap-2">
          <Button
            type="button"
            variant="outline"
            size="sm"
            disabled={!pageInfo.hasPreviousPage}
            onClick={() =>
              setPage((currentPage) => Math.max(0, currentPage - 1))
            }
          >
            {t('actions.previous')}
          </Button>
          <Button
            type="button"
            variant="outline"
            size="sm"
            disabled={!pageInfo.hasNextPage}
            onClick={() => setPage((currentPage) => currentPage + 1)}
          >
            {t('actions.next')}
          </Button>
        </div>
      )}

      <Dialog
        open={selectedReport !== null}
        onOpenChange={(open) => {
          if (!open) {
            setSelectedReport(null);
          }
        }}
      >
        <DialogContent>
          <DialogHeader>
            <DialogTitle>{t('hide-dialog.title')}</DialogTitle>
          </DialogHeader>

          <div className="space-y-4">
            <div className="space-y-2">
              <Label>{t('hide-dialog.hiddenReason')}</Label>
              <Textarea
                value={hiddenReason}
                onChange={(event) => setHiddenReason(event.target.value)}
                maxLength={1000}
              />
            </div>
            <div className="space-y-2">
              <Label>{t('hide-dialog.resolutionNote')}</Label>
              <Textarea
                value={resolutionNote}
                onChange={(event) => setResolutionNote(event.target.value)}
                maxLength={1000}
              />
            </div>
          </div>

          <DialogFooter>
            <Button
              type="button"
              variant="outline"
              onClick={() => setSelectedReport(null)}
            >
              {t('actions.cancel')}
            </Button>
            <Button
              type="button"
              variant="destructive"
              isPending={isReviewPending}
              onClick={hidePost}
            >
              {t('actions.hide')}
            </Button>
          </DialogFooter>
        </DialogContent>
      </Dialog>
    </div>
  );
}

function formatDate(value: string) {
  return new Intl.DateTimeFormat('ko-KR', {
    dateStyle: 'medium',
    timeStyle: 'short',
  }).format(new Date(value));
}

function PostContentPreview({ content }: { content: string }) {
  const editor = useEditor({
    extensions: [StarterKit, ImageNode],
    content: toEditorContent(content),
    editable: false,
    immediatelyRender: false,
  });

  return <EditorContent editor={editor} />;
}

function toEditorContent(content: string): JSONContent {
  try {
    const parsed = JSON.parse(content) as JSONContent;
    if (parsed.type) {
      return deserialize(content, []);
    }
    if (typeof parsed.text === 'string') {
      return textContent(parsed.text);
    }
  } catch {
    return textContent(content);
  }

  return textContent(content);
}

function textContent(content: string): JSONContent {
  return {
    type: 'doc',
    content: [
      {
        type: 'paragraph',
        content: content
          ? [
              {
                type: 'text',
                text: content,
              },
            ]
          : [],
      },
    ],
  };
}
