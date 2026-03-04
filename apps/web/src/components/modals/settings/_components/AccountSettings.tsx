'use client';

import { GoogleLogoIcon } from '@phosphor-icons/react';
import { useTranslations } from 'next-intl';
import { useRouter } from 'next/navigation';
import { forwardRef, useImperativeHandle } from 'react';
import { toast } from 'sonner';

import { Button } from '@/components/ui/button';
import { useDeleteOAuth2AccountMutation } from '@/features/user/api/delete-oauth2-account';
import { useGetOAuth2AccountsQuery } from '@/features/user/api/get-oauth2-accounts';
import { getOAuth2AuthorizationUrl } from '@/features/user/util/oauth2';
import SyncError, { ErrorCode } from '@/lib/error';
import { OAuth2Provider } from '@/types/profile';

import { SettingsCategoryRef } from '..';

const OAuth2Providers: {
  id: OAuth2Provider;
  icon: React.ReactNode;
}[] = [
  {
    id: 'GOOGLE',
    icon: <GoogleLogoIcon />,
  },
];

const AccountSettings = forwardRef<SettingsCategoryRef>(({}, ref) => {
  const t = useTranslations('modals.settings.categories.account');
  const router = useRouter();

  const { data: oauth2Accounts } = useGetOAuth2AccountsQuery();
  const { mutate: deleteOAuth2Account } = useDeleteOAuth2AccountMutation();

  useImperativeHandle(ref, () => ({
    submit: () => {},
    reset: () => {},
  }));

  return (
    <>
      <div>
        <h2 className="font-bold">{t('oauth2.title')}</h2>
        <p className="text-xs mb-4">{t('oauth2.description')}</p>

        <div className="flex flex-col gap-2 mx-10">
          {OAuth2Providers.map((provider) => ({
            ...provider,
            connected:
              oauth2Accounts?.some(
                (account) => account.provider === provider.id,
              ) || false,
          })).map((provider) => (
            <Button
              key={provider.id}
              variant="outline"
              onClick={() => {
                if (provider.connected) {
                  deleteOAuth2Account(provider.id, {
                    onError: (error) => {
                      if (error instanceof SyncError) {
                        const { code } = error;

                        if (
                          code === ErrorCode.OAUTH2_ACCOUNT_CANNOT_BE_DELETED
                        ) {
                          toast.error(t('oauth2.errors.cannot_be_deleted'));
                        }
                      }
                    },
                  });
                } else {
                  router.push(getOAuth2AuthorizationUrl(provider.id));
                }
              }}
            >
              {provider.icon}
              <span>
                {provider.connected
                  ? t(`oauth2.connected.${provider.id}`)
                  : t(`oauth2.connect.${provider.id}`)}
              </span>
            </Button>
          ))}
        </div>
      </div>
    </>
  );
});

AccountSettings.displayName = 'AccountSettings';

export default AccountSettings;
