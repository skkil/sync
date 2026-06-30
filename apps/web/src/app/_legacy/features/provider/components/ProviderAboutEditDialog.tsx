'use client';

import { zodResolver } from '@hookform/resolvers/zod';
import { useTranslations } from 'next-intl';
import { useEffect, useMemo, useState } from 'react';
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
import { Field, FieldGroup, FieldLabel } from '@/components/ui/field';
import { Input } from '@/components/ui/input';
import { Textarea } from '@/components/ui/textarea';
import useGetProviderQuery from '@/features/provider/api/get-provider';
import { ProviderType } from '@/types/provider';

const ProviderAboutEditFormSchema = z.object({
  longDescription: z.string(),
  website: z.string(),
  industry: z.string(),
  size: z.string(),
  location: z.string(),
  subcategory: z.string(),
});

type ProviderAboutEditFormValues = z.infer<typeof ProviderAboutEditFormSchema>;

interface ProviderAboutEditDialogProps {
  id: string;
  onSave?: (values: ProviderAboutEditFormValues) => void | Promise<void>;
}

const EMPTY_VALUES: ProviderAboutEditFormValues = {
  longDescription: '',
  website: '',
  industry: '',
  size: '',
  location: '',
  subcategory: '',
};

export default function ProviderAboutEditDialog({
  id,
  onSave,
}: ProviderAboutEditDialogProps) {
  const tProvider = useTranslations('pages.provider');
  const t = useTranslations('pages.provider.about.edit-dialog');

  const [open, setOpen] = useState(false);
  const { data: provider } = useGetProviderQuery(id);

  const defaultValues = useMemo<ProviderAboutEditFormValues>(() => {
    if (!provider) {
      return EMPTY_VALUES;
    }

    return {
      longDescription: '',
      website: provider.contactInfo?.trim() ?? '',
      industry: provider.industry?.trim() ?? '',
      size: '',
      location: '',
      subcategory: '',
    };
  }, [provider]);

  const form = useForm<ProviderAboutEditFormValues>({
    resolver: zodResolver(ProviderAboutEditFormSchema),
    defaultValues: EMPTY_VALUES,
  });

  useEffect(() => {
    if (open) {
      form.reset(defaultValues);
    }
  }, [defaultValues, form, open]);

  const isSchool = provider?.type === ProviderType.SCHOOL;
  const canSubmit = typeof onSave === 'function';

  const formSubmitHandler = form.handleSubmit(async (values) => {
    if (!onSave) {
      return;
    }

    await onSave(values);
    setOpen(false);
  });

  return (
    <Dialog open={open} onOpenChange={setOpen}>
      <DialogTrigger asChild>
        <Button type="button" disabled={!provider}>
          {tProvider('about.actions.edit')}
        </Button>
      </DialogTrigger>

      <DialogContent className="grid max-h-[85vh] grid-rows-[auto_minmax(0,1fr)] overflow-hidden sm:max-w-xl">
        <DialogHeader>
          <DialogTitle>{t('title')}</DialogTitle>
          <DialogDescription>{t('description')}</DialogDescription>
        </DialogHeader>

        <form
          onSubmit={formSubmitHandler}
          className="grid min-h-0 grid-rows-[minmax(0,1fr)_auto]"
        >
          <div className="min-h-0 overflow-y-auto px-1 py-1">
            <FieldGroup className="pb-1">
              <Controller
                name="longDescription"
                control={form.control}
                render={({ field }) => (
                  <Field>
                    <FieldLabel>{t('fields.longDescription')}</FieldLabel>
                    <Textarea {...field} rows={5} />
                  </Field>
                )}
              />

              <Controller
                name="website"
                control={form.control}
                render={({ field }) => (
                  <Field>
                    <FieldLabel>{t('fields.website')}</FieldLabel>
                    <Input {...field} />
                  </Field>
                )}
              />

              {!isSchool && (
                <Controller
                  name="industry"
                  control={form.control}
                  render={({ field }) => (
                    <Field>
                      <FieldLabel>{t('fields.industry')}</FieldLabel>
                      <Input {...field} />
                    </Field>
                  )}
                />
              )}

              {!isSchool && (
                <Controller
                  name="size"
                  control={form.control}
                  render={({ field }) => (
                    <Field>
                      <FieldLabel>{t('fields.size')}</FieldLabel>
                      <Input {...field} />
                    </Field>
                  )}
                />
              )}

              <Controller
                name="location"
                control={form.control}
                render={({ field }) => (
                  <Field>
                    <FieldLabel>{t('fields.location')}</FieldLabel>
                    <Input {...field} />
                  </Field>
                )}
              />

              {!isSchool && (
                <Controller
                  name="subcategory"
                  control={form.control}
                  render={({ field }) => (
                    <Field>
                      <FieldLabel>{t('fields.subcategory')}</FieldLabel>
                      <Input {...field} />
                    </Field>
                  )}
                />
              )}
            </FieldGroup>
          </div>

          <DialogFooter>
            <div className="flex w-full items-center justify-end gap-2">
              <Button
                type="button"
                variant="outline"
                onClick={() => setOpen(false)}
              >
                {t('actions.cancel')}
              </Button>
              <Button
                type="submit"
                disabled={
                  !canSubmit ||
                  !form.formState.isDirty ||
                  form.formState.isSubmitting
                }
              >
                {t('actions.save')}
              </Button>
            </div>
          </DialogFooter>
        </form>
      </DialogContent>
    </Dialog>
  );
}
