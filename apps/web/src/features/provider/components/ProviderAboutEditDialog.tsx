'use client';

import { useEffect, useMemo, useState } from 'react';
import { useTranslations } from 'next-intl';

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
import { ProviderType } from '@/types/provider';

interface ProviderAboutEditDialogProps {
  providerType: ProviderType;
  triggerLabel: string;
  defaultValues: {
    description: string;
    longDescription: string;
    website: string;
    industry: string;
    size: string;
    location: string;
    subcategory: string;
  };
}

export default function ProviderAboutEditDialog({
  providerType,
  triggerLabel,
  defaultValues,
}: ProviderAboutEditDialogProps) {
  const t = useTranslations('pages.provider.about.edit-dialog');
  const [open, setOpen] = useState(false);
  const [formValues, setFormValues] = useState(defaultValues);

  const isSchool = providerType === ProviderType.SCHOOL;
  const hasChanges = useMemo(
    () =>
      formValues.longDescription !== defaultValues.longDescription ||
      formValues.website !== defaultValues.website ||
      formValues.industry !== defaultValues.industry ||
      formValues.size !== defaultValues.size ||
      formValues.location !== defaultValues.location ||
      formValues.subcategory !== defaultValues.subcategory,
    [defaultValues, formValues],
  );

  useEffect(() => {
    if (open) {
      setFormValues(defaultValues);
    }
  }, [defaultValues, open]);

  return (
    <Dialog open={open} onOpenChange={setOpen}>
      <DialogTrigger asChild>
        <Button type="button">{triggerLabel}</Button>
      </DialogTrigger>

      <DialogContent className="max-h-[85vh] sm:max-w-xl grid-rows-[auto_minmax(0,1fr)_auto] overflow-hidden">
        <DialogHeader>
          <DialogTitle>{t('title')}</DialogTitle>
          <DialogDescription>{t('description')}</DialogDescription>
        </DialogHeader>

        <div className="min-h-0 overflow-y-auto px-1 py-1">
          <FieldGroup className="pb-1">
            <Field>
              <FieldLabel>{t('fields.longDescription')}</FieldLabel>
              <Textarea
                value={formValues.longDescription}
                onChange={(event) =>
                  setFormValues((prev) => ({
                    ...prev,
                    longDescription: event.target.value,
                  }))
                }
                rows={5}
              />
            </Field>

            <Field>
              <FieldLabel>{t('fields.website')}</FieldLabel>
              <Input
                value={formValues.website}
                onChange={(event) =>
                  setFormValues((prev) => ({
                    ...prev,
                    website: event.target.value,
                  }))
                }
              />
            </Field>

            {!isSchool && (
              <Field>
                <FieldLabel>{t('fields.industry')}</FieldLabel>
                <Input
                  value={formValues.industry}
                  onChange={(event) =>
                    setFormValues((prev) => ({
                      ...prev,
                      industry: event.target.value,
                    }))
                  }
                />
              </Field>
            )}

            {!isSchool && (
              <Field>
                <FieldLabel>{t('fields.size')}</FieldLabel>
                <Input
                  value={formValues.size}
                  onChange={(event) =>
                    setFormValues((prev) => ({
                      ...prev,
                      size: event.target.value,
                    }))
                  }
                />
              </Field>
            )}

            <Field>
              <FieldLabel>{t('fields.location')}</FieldLabel>
              <Input
                value={formValues.location}
                onChange={(event) =>
                  setFormValues((prev) => ({
                    ...prev,
                    location: event.target.value,
                  }))
                }
              />
            </Field>

            {!isSchool && (
              <Field>
                <FieldLabel>{t('fields.subcategory')}</FieldLabel>
                <Input
                  value={formValues.subcategory}
                  onChange={(event) =>
                    setFormValues((prev) => ({
                      ...prev,
                      subcategory: event.target.value,
                    }))
                  }
                />
              </Field>
            )}
          </FieldGroup>
        </div>

        <DialogFooter>
          <div className="w-full flex items-center justify-end gap-2">
            <Button type="button" variant="outline" onClick={() => setOpen(false)}>
              {t('actions.cancel')}
            </Button>
            <Button type="button" disabled={!hasChanges}>
              {t('actions.save')}
            </Button>
          </div>
        </DialogFooter>
      </DialogContent>
    </Dialog>
  );
}
