'use client';

import { SignInIcon } from '@phosphor-icons/react';
import { useTranslations } from 'next-intl';
import { useRouter } from 'next/navigation';

import OAuthProviders from '@/app/auth/_components/OAuthProviders';
import LoginForm from '@/app/auth/login/_components/LoginForm';
import { Button } from '@/components/ui/button';
import {
  Dialog,
  DialogClose,
  DialogContent,
  DialogDescription,
  DialogHeader,
  DialogTitle,
} from '@/components/ui/dialog';
import { Separator } from '@/components/ui/separator';
import { useModal } from '@/hooks/store';
import type { AuthRequiredModalPayload } from '@/store/slices/modal';

const DEFAULT_PAYLOAD: AuthRequiredModalPayload = {
  intent: 'write',
};

export default function AuthRequiredModal() {
  const t = useTranslations('modals.auth-required');
  const router = useRouter();
  const { isOpen, payload, closeModal } = useModal();
  const authPayload = (payload ?? DEFAULT_PAYLOAD) as AuthRequiredModalPayload;

  return (
    <Dialog
      open={isOpen}
      onOpenChange={(open) => {
        if (!open) {
          closeModal();
        }
      }}
    >
      <DialogContent className="sm:max-w-md">
        <DialogHeader>
          <div className="flex items-start justify-between gap-4">
            <div className="flex min-w-0 flex-col gap-2">
              <div className="bg-primary/10 text-primary flex size-10 items-center justify-center rounded-full">
                <SignInIcon />
              </div>
              <DialogTitle>{t('title')}</DialogTitle>
              <DialogDescription>
                {t(`descriptions.${authPayload.intent}`)}
              </DialogDescription>
            </div>
            <DialogClose />
          </div>
        </DialogHeader>

        <LoginForm
          onSuccess={() => {
            closeModal();

            if (authPayload.redirectTo) {
              router.push(authPayload.redirectTo);
              return;
            }

            router.refresh();
          }}
          redirectTo={authPayload.redirectTo}
        />

        <div className="flex items-center gap-3">
          <Separator className="flex-1" />
          <span className="text-muted-foreground text-xs">
            {t('oauth-divider')}
          </span>
          <Separator className="flex-1" />
        </div>

        <OAuthProviders />

        <Button variant="ghost" onClick={closeModal}>
          {t('actions.keep-reading')}
        </Button>
      </DialogContent>
    </Dialog>
  );
}
