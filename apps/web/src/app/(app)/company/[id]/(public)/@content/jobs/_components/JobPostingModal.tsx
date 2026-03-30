'use client';

import { useTranslations } from 'next-intl';

import {
  Dialog,
  DialogClose,
  DialogContent,
  DialogDescription,
  DialogHeader,
  DialogTitle,
} from '@/components/ui/dialog';

import { JobPosting } from './JobPostingsTableColumns';

interface JobPostingModalProps {
  open: boolean;
  onOpenChange: (open: boolean) => void;
  jobPosting: JobPosting | null;
}

export default function JobPostingModal({
  jobPosting,
  open,
  onOpenChange,
}: JobPostingModalProps) {
  const t = useTranslations('pages.company.job-postings.modal');

  if (!jobPosting) {
    return null;
  }

  return (
    <Dialog open={open} onOpenChange={onOpenChange}>
      <DialogContent className="w-11/12 sm:max-w-4xl min-h-[60vh] max-h-[90vh] overflow-y-auto flex flex-col">
        <DialogHeader>
          <div className="flex justify-between items-center">
            <div className="flex flex-col gap-2">
              <DialogTitle className="text-2xl">
                {jobPosting.jobTitle}
              </DialogTitle>
              <DialogDescription className="flex items-center gap-2">
                {jobPosting.location && (
                  <>
                    <span>{jobPosting.location}</span>|
                  </>
                )}
                <span>
                  {t('posted-on')}{' '}
                  {new Date(jobPosting.createdAt).toLocaleDateString()}
                </span>
              </DialogDescription>
            </div>

            <DialogClose />
          </div>
        </DialogHeader>

        <div className="space-y-4 grow">
          <div>
            <p className="text-sm text-muted-foreground whitespace-pre-wrap">
              {jobPosting.jobDescription}
            </p>
          </div>
        </div>
      </DialogContent>
    </Dialog>
  );
}
