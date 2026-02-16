import { zodResolver } from '@hookform/resolvers/zod';
import { PencilIcon } from '@phosphor-icons/react';
import { useTranslations } from 'next-intl';
import { useMemo, useState } from 'react';
import { Controller, useForm } from 'react-hook-form';
import z from 'zod';

import { Button } from '@/components/ui/button';
import {
  Dialog,
  DialogContent,
  DialogDescription,
  DialogFooter,
  DialogHeader,
  DialogTitle,
  DialogTrigger,
} from '@/components/ui/dialog';
import { Field, FieldLabel } from '@/components/ui/field';
import { Input } from '@/components/ui/input';
import { useCreateExperienceMutation } from '@/features/experience/api/create-experience';
import SelectProviders from '@/features/provider/components/SelectProviders';
import { getProviderForExperience as getProviderTypeForExperience } from '@/features/provider/util';
import { ExperienceType } from '@/types/experience';
import { ProviderType } from '@/types/provider';

interface AddExperienceButtonProps {
  type: ExperienceType;
}

const AddExperienceFormSchema = (t: ReturnType<typeof useTranslations>) =>
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

function AddExperienceForm({
  type,
  onClose,
}: {
  type: ExperienceType;
  onClose: () => void;
}) {
  const t = useTranslations('pages.add-experience.form');

  const schema = useMemo(() => AddExperienceFormSchema(t), [t]);
  type AddExperienceFormValues = z.infer<typeof schema>;

  const form = useForm<AddExperienceFormValues>({
    resolver: zodResolver(schema),
    defaultValues: {
      type,
      provider: {
        id: '',
        name: '',
      },
      major: '',
      gpa: 4.5,
    },
  });

  const { mutate: createExperience } = useCreateExperienceMutation();

  const formSubmitHandler = (values: AddExperienceFormValues) => {
    createExperience(
      {
        ...values,
        providerId: values.provider.id,
        startDate: new Date(),
      },
      {
        onSuccess: () => {
          onClose();
        },
      },
    );
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
  value,
  type,
  invalid,
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
        types={[getProviderTypeForExperience(type) as ProviderType]}
        value={value.id}
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
  const t = useTranslations('pages.add-experience.form');

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

export default function AddExperienceButton({
  type,
}: AddExperienceButtonProps) {
  const t = useTranslations('pages.add-experience');

  const [open, setOpen] = useState(false);

  return (
    <Dialog open={open} onOpenChange={setOpen}>
      <DialogTrigger asChild>
        <Button variant="ghost" size="icon">
          <PencilIcon />
        </Button>
      </DialogTrigger>

      <DialogContent>
        <DialogHeader>
          <DialogTitle>{t(`title.${type}`)}</DialogTitle>
          <DialogDescription>{t(`description.${type}`)}</DialogDescription>
        </DialogHeader>

        <AddExperienceForm type={type} onClose={() => setOpen(false)} />
      </DialogContent>
    </Dialog>
  );
}
