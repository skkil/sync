'use client';

import { useTranslations } from 'next-intl';
import { useState } from 'react';
import { toast } from 'sonner';

import { useReportPost } from '@/api/__generated__/post-report/post-report';
import { ReportPostRequestReason } from '@/api/__generated__/types/ReportPostRequestReason';
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
import { Textarea } from '@/components/ui/textarea';
import { useSession } from '@/lib/auth/client';

interface ReportPostDialogProps {
  postId: number;
  open: boolean;
  onOpenChange: (open: boolean) => void;
}

export function ReportPostDialog({
  postId,
  open,
  onOpenChange,
}: ReportPostDialogProps) {
  const t = useTranslations('pages.posts.report');
  const { data: session } = useSession();
  const { mutate: reportPost, isPending } = useReportPost();
  const [reason, setReason] = useState<ReportPostRequestReason>(
    ReportPostRequestReason.Spam,
  );
  const [description, setDescription] = useState('');

  const submitReport = () => {
    if (!session?.user) {
      toast.error(t('messages.login-required'));
      return;
    }

    reportPost(
      {
        postId: String(postId),
        data: {
          reason,
          description: description.trim() || null,
        },
      },
      {
        onSuccess: () => {
          toast.success(t('messages.success'));
          setDescription('');
          setReason(ReportPostRequestReason.Spam);
          onOpenChange(false);
        },
        onError: () => {
          toast.error(t('messages.error'));
        },
      },
    );
  };

  return (
    <Dialog open={open} onOpenChange={onOpenChange}>
      <DialogContent>
        <DialogHeader>
          <DialogTitle>{t('title')}</DialogTitle>
        </DialogHeader>

        <div className="space-y-4">
          <div className="space-y-2">
            <Label>{t('fields.reason')}</Label>
            <Select
              value={reason}
              onValueChange={(value) =>
                setReason(value as ReportPostRequestReason)
              }
            >
              <SelectTrigger className="w-full">
                <SelectValue />
              </SelectTrigger>
              <SelectContent>
                {Object.values(ReportPostRequestReason).map((value) => (
                  <SelectItem key={value} value={value}>
                    {t(`reasons.${value}`)}
                  </SelectItem>
                ))}
              </SelectContent>
            </Select>
          </div>

          <div className="space-y-2">
            <Label>{t('fields.description')}</Label>
            <Textarea
              value={description}
              maxLength={1000}
              onChange={(event) => setDescription(event.target.value)}
              placeholder={t('fields.description-placeholder')}
            />
          </div>
        </div>

        <DialogFooter>
          <Button
            type="button"
            variant="outline"
            onClick={() => onOpenChange(false)}
          >
            {t('actions.cancel')}
          </Button>
          <Button
            type="button"
            variant="destructive"
            isPending={isPending}
            onClick={submitReport}
          >
            {t('actions.submit')}
          </Button>
        </DialogFooter>
      </DialogContent>
    </Dialog>
  );
}
