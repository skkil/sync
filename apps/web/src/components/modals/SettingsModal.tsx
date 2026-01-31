'use client';

import { useTranslations } from 'next-intl';
import { useEffect, useState } from 'react';

import { useModal } from '@/hooks/store';
import { useSession } from '@/lib/auth/client';
import { server } from '@/lib/server/client';
import { UpdateProfileRequest } from '@/types/api/users';

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
import { Input } from '../ui/input';
import { Label } from '../ui/label';

export default function SettingsModal() {
  const t = useTranslations('modals.settings');

  const { isOpen, closeModal } = useModal();

  const [name, setName] = useState('');
  const { data: session } = useSession();

  useEffect(() => {
    if (session?.user?.name) {
      setName(session.user.name);
    }
  }, [session?.user?.name]);

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

        <div className="grow flex flex-col gap-2">
          <Label>{t('profile.name.label')}</Label>
          <Input
            type="text"
            value={name}
            onChange={(e) => setName(e.target.value)}
          />
        </div>

        <DialogFooter>
          <div className="flex gap-2">
            <Button
              variant="destructive"
              onClick={() => {
                server
                  .patch('profile/me', {
                    json: {
                      name,
                    } satisfies UpdateProfileRequest,
                  })
                  .catch(() => {
                    // TODO: Proper error handling
                    console.error('Failed to update profile');
                  });
              }}
            >
              {t('actions.save')}
            </Button>
            <Button
              variant="outline"
              onClick={() => {
                closeModal();
              }}
            >
              {t('actions.cancel')}
            </Button>
          </div>
        </DialogFooter>
      </DialogContent>
    </Dialog>
  );
}
