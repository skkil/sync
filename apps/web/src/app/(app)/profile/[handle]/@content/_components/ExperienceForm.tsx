import { zodResolver } from '@hookform/resolvers/zod';
import { useTranslations } from 'next-intl';
import { useMemo } from 'react';
import { Controller, useForm } from 'react-hook-form';
import z from 'zod';

import { Button } from '@/components/ui/button';
import { DialogFooter } from '@/components/ui/dialog';
import { Field, FieldLabel } from '@/components/ui/field';
import { Input } from '@/components/ui/input';
import SelectProviders from '@/features/provider/components/SelectProviders';
import { getProviderTypeForExperienceType } from '@/features/provider/util';
import { Experience, ExperienceType } from '@/types/experience';
import { ProviderType } from '@/types/provider';

interface ExperienceFormProps {
  type: ExperienceType;
  experience?: Experience;
  onSubmit: (values: ExperienceFormValues) => void;
  onClose: () => void;
}

const ExperienceFormSchema = (t: ReturnType<typeof useTranslations>) =>
  z.discriminatedUnion('type', [
    z.object({
      type: z.enum(ExperienceType).extract(['EMPLOYMENT']),
      provider: z.object({
        id: z.string().min(1, t('errors.required-provider')),
        name: z.string(),
      }),
    }),
    z.object({
      type: z.enum(ExperienceType).extract(['EDUCATION']),
      provider: z.object({
        id: z.string().min(1, t('errors.required-provider')),
        name: z.string(),
      }),
      major: z.string().min(1, t('errors.required-major')),
      gpa: z.number().min(0).max(4.5),
    }),
  ]);

export type ExperienceFormValues = z.infer<
  ReturnType<typeof ExperienceFormSchema>
>;

export default function ExperienceForm({
  type,
  experience,
  onSubmit,
  onClose,
}: ExperienceFormProps) {
  const t = useTranslations('pages.profile.experience.form');

  const schema = useMemo(() => ExperienceFormSchema(t), [t]);

  const form = useForm<ExperienceFormValues>({
    resolver: zodResolver(schema),
    defaultValues: getDefaultValues(type, experience),
  });

  const formSubmitHandler = (values: ExperienceFormValues) => {
    onSubmit(values);
  };

  return (
    <form onSubmit={form.handleSubmit(formSubmitHandler)}>
      <div className="flex flex-col gap-4">
        <Controller
          name="provider"
          control={form.control}
          render={({ field, fieldState }) => (
            <ProviderField
              value={field.value}
              type={type}
              invalid={fieldState.invalid}
              onChange={field.onChange}
            />
          )}
        />

        {type === ExperienceType.EDUCATION && (
          <Controller
            name="major"
            control={form.control}
            render={({ field, fieldState }) => (
              <MajorField
                value={field.value}
                onChange={field.onChange}
                invalid={fieldState.invalid}
              />
            )}
          />
        )}
      </div>

      <DialogFooter className="mt-4">
        <div className="flex gap-2">
          <Button type="button" variant="destructive" onClick={onClose}>
            {t('actions.cancel.label')}
          </Button>
          <Button type="submit">{t('actions.submit.label')}</Button>
        </div>
      </DialogFooter>
    </form>
  );
}

function ProviderField({
  type,
  invalid,
  value,
  onChange,
}: {
  value: {
    id: string;
    name: string;
  };
  type: ExperienceType;
  invalid?: boolean;
  onChange: (value: { id: string; name: string }) => void;
}) {
  return (
    <Field data-invalid={invalid}>
      <SelectProviders
        defaultValue={{
          type: getProviderTypeForExperienceType(type),
          ...value,
        }}
        types={[getProviderTypeForExperienceType(type) as ProviderType]}
        onChange={(values) => {
          onChange(values);
        }}
      />
    </Field>
  );
}

function MajorField({
  value,
  onChange,
  invalid,
}: {
  value: string;
  onChange: (value: string) => void;
  invalid?: boolean;
}) {
  const t = useTranslations('pages.profile.experience.form');

  return (
    <Field data-invalid={invalid}>
      <div className="flex items-center justify-between">
        <FieldLabel>{t('major.label')}</FieldLabel>
      </div>

      <Input
        value={value}
        onChange={(e) => onChange(e.target.value)}
        placeholder={t('major.placeholder')}
        aria-invalid={invalid}
        type="text"
      />
    </Field>
  );
}

function getDefaultValues(
  type: ExperienceType,
  initial?: Experience,
): ExperienceFormValues {
  const base = {
    ...initial,
    provider: {
      id: initial?.provider.id || '',
      name: initial?.provider.name || '',
    },
  };

  switch (type) {
    case ExperienceType.EMPLOYMENT:
      return {
        ...base,
        type: ExperienceType.EMPLOYMENT,
      };
    case ExperienceType.EDUCATION:
      return {
        ...base,
        type: ExperienceType.EDUCATION,
        ...(initial?.type === ExperienceType.EDUCATION
          ? {
              major: initial.major || '',
              gpa: initial.gpa || 0,
            }
          : {
              major: '',
              gpa: 0,
            }),
      };
  }
}
