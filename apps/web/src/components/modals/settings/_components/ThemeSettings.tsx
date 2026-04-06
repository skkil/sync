import { useTranslations } from 'next-intl';
import { useTheme } from 'next-themes';
import { forwardRef, useEffect, useImperativeHandle, useState } from 'react';

import { useUpdateUserPreferences } from '@/api/__generated__/preferences/preferences';
import { Field, FieldDescription, FieldLabel } from '@/components/ui/field';
import { FieldContent, FieldTitle } from '@/components/ui/field';
import { RadioGroup, RadioGroupItem } from '@/components/ui/radio-group';
import { useSession } from '@/lib/auth/client';

import { SettingsCategoryRef } from '..';
import { SettingsSubTitle } from './ui/title';

const ThemeSettings = forwardRef<SettingsCategoryRef>(({}, ref) => {
  const t = useTranslations('modals.settings.categories.theme');

  const { data: session } = useSession();

  const { setTheme } = useTheme();
  const [selectedTheme, setSelectedTheme] = useState('');

  const { mutate: updateUserPreferences } = useUpdateUserPreferences();

  useEffect(() => {
    if (session) {
      setSelectedTheme(session.user.theme);
    }
  }, [session]);

  useImperativeHandle(ref, () => ({
    submit: () => {
      updateUserPreferences(
        {
          data: {
            theme: selectedTheme,
          },
        },
        {
          onSuccess: () => {
            setTheme(selectedTheme);
          },
        },
      );
    },
    reset: () => {},
  }));

  return (
    <div>
      <div>
        <SettingsSubTitle>{t('theme.label')}</SettingsSubTitle>
        <RadioGroup
          defaultValue="system"
          value={selectedTheme}
          onValueChange={setSelectedTheme}
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
