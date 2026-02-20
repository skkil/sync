'use client';

import { useTranslations } from 'next-intl';

import { useModal } from '@/hooks/store';

import { Button } from '../ui/button';
import {
  Dialog,
  DialogClose,
  DialogContent,
  DialogDescription,
  DialogFooter,
  DialogHeader,
  DialogTitle,
} from '../ui/dialog';

export default function SettingsModal() {
  const t = useTranslations('modals.settings');
  const { isOpen, closeModal } = useModal();

  return (
    <Dialog
      open={isOpen}
      onOpenChange={(open) => {
        if (!open) {
          closeModal();
        }
      }}
    >
      <DialogContent className="w-11/12 sm:max-w-md">
        <DialogHeader>
          <div className="flex justify-between">
            <div className="flex flex-col gap-2">
              <DialogTitle>{t('title')}</DialogTitle>
              <DialogDescription>{t('description')}</DialogDescription>
            </div>

            <DialogClose />
          </div>
        </DialogHeader>

        <DialogFooter>
          <div className="flex gap-2">
            <Button
              variant="outline"
              onClick={() => {
                closeModal();
              }}
            >
              {t('actions.cancel')}
            </Button>
            <Button
              onClick={() => {
                closeModal();
              }}
            >
              {t('actions.save')}
            </Button>
          </div>
        </DialogFooter>
      </DialogContent>
    </Dialog>
  );
}
