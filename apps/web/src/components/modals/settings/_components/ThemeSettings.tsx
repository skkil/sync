import { useTranslations } from 'next-intl';
import { useTheme } from 'next-themes';
import { forwardRef, useEffect, useImperativeHandle, useRef } from 'react';
import { toast } from 'sonner';

import { useUpdateUserPreferences } from '@/api/__generated__/preferences/preferences';
import { Field, FieldDescription, FieldLabel } from '@/components/ui/field';
import { FieldContent, FieldTitle } from '@/components/ui/field';
import { RadioGroup, RadioGroupItem } from '@/components/ui/radio-group';
import { useSession } from '@/lib/auth/client';
import SyncError, { ErrorCode } from '@/lib/error';

import { SettingsCategoryRef } from '..';
import { SettingsSubTitle } from './ui/title';

const ThemeSettings = forwardRef<SettingsCategoryRef>(({}, ref) => {
  const t = useTranslations('modals.settings.categories.theme');

  const { data: session } = useSession();

  const { theme: previewedTheme, setTheme: setPreviewedTheme } = useTheme();
  const persistedThemeRef = useRef('system');
  const hasInitializedRef = useRef(false);

  const { mutate: updateUserPreferences } = useUpdateUserPreferences();

  useEffect(() => {
    if (session && !hasInitializedRef.current) {
      hasInitializedRef.current = true;
      persistedThemeRef.current = session.user.theme;
      setPreviewedTheme(session.user.theme);
    }
  }, [session, setPreviewedTheme]);

  useImperativeHandle(ref, () => ({
    submit: () => {
      const theme = previewedTheme ?? persistedThemeRef.current;

      updateUserPreferences(
        {
          data: {
            theme,
          },
        },
        {
          onSuccess: () => {
            persistedThemeRef.current = theme;
            toast.success(t('messages.success'));
          },
          onError: (error) => {
            setPreviewedTheme(persistedThemeRef.current);

            if (
              error instanceof SyncError &&
              error.code === ErrorCode.NETWORK_ERROR
            ) {
              toast.error(t('errors.network'));
            } else {
              toast.error(t('errors.unknown'));
            }
          },
        },
      );
    },
    reset: () => {
      setPreviewedTheme(persistedThemeRef.current);
    },
  }));

  return (
    <div>
      <div>
        <SettingsSubTitle>{t('theme.label')}</SettingsSubTitle>
        <RadioGroup
          defaultValue="system"
          value={previewedTheme}
          onValueChange={setPreviewedTheme}
        >
          <FieldLabel htmlFor="light">
            <Field orientation="horizontal">
              <FieldContent>
                <FieldTitle>{t('theme.options.light.label')}</FieldTitle>
                <FieldDescription>
                  {t('theme.options.light.description')}
                </FieldDescription>
              </FieldContent>
              <RadioGroupItem value="light" id="light" />
            </Field>
          </FieldLabel>
          <FieldLabel htmlFor="dark">
            <Field orientation="horizontal">
              <FieldContent>
                <FieldTitle>{t('theme.options.dark.label')}</FieldTitle>
                <FieldDescription>
                  {t('theme.options.dark.description')}
                </FieldDescription>
              </FieldContent>
              <RadioGroupItem value="dark" id="dark" />
            </Field>
          </FieldLabel>
          <FieldLabel htmlFor="system">
            <Field orientation="horizontal">
              <FieldContent>
                <FieldTitle>{t('theme.options.system.label')}</FieldTitle>
                <FieldDescription>
                  {t('theme.options.system.description')}
                </FieldDescription>
              </FieldContent>
              <RadioGroupItem value="system" id="system" />
            </Field>
          </FieldLabel>
        </RadioGroup>
      </div>
    </div>
  );
});

ThemeSettings.displayName = 'ThemeSettings';

export default ThemeSettings;
