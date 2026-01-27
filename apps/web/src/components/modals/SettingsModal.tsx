'use client';

import { useTranslations } from 'next-intl';
import { useEffect, useState } from 'react';

import { useModal } from '@/hooks/store';
import { useSession } from '@/lib/auth/client';
import { server } from '@/lib/server/client';

import { Button } from '../ui/button';
import {
  Dialog,
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

  const { closeModal } = useModal();

  const [name, setName] = useState('');
  const { isPending, data: session } = useSession();

  useEffect(() => {
    if (session?.user?.name) {
      setName(session.user.name);
    }
  }, [isPending]);

  return (
    <Dialog open={true}>
      <DialogContent className="min-w-7xl h-5/6 flex flex-col">
        <DialogHeader>
          <DialogTitle>{t('title')}</DialogTitle>
          <DialogDescription>{t('description')}</DialogDescription>
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
              variant="outline"
              onClick={() => {
                server
                  .patch('profile/me', {
                    json: {
                      name,
                    } satisfies UpdateProfileRequest,
                  })
                  .then(() => {});
              }}
            >
              {t('actions.save')}
            </Button>
            <Button
              variant="destructive"
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
