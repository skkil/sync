'use client';

import { zodResolver } from '@hookform/resolvers/zod';
import { PlusIcon } from '@phosphor-icons/react';
import { useTranslations } from 'next-intl';
import { useMemo, useState } from 'react';
import { Controller, useForm } from 'react-hook-form';
import { toast } from 'sonner';
import z from 'zod';

import { useCreateContestOccurrence } from '@/api/__generated__/contests/contests';
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
import {
  Field,
  FieldError,
  FieldGroup,
  FieldLabel,
} from '@/components/ui/field';
import { Input } from '@/components/ui/input';
import { Textarea } from '@/components/ui/textarea';

interface CreateContestOccurrenceFormProps {
  contestId: string;
}

const createContestOccurrenceFormSchema = (t: (key: string) => string) =>
  z.object({
    title: z.string().min(1, t('form.errors.required-title')),
    description: z.string(),
  });

type CreateContestOccurrenceFormValues = z.infer<
  ReturnType<typeof createContestOccurrenceFormSchema>
>;

export default function CreateContestOccurrenceForm({
  contestId,
}: CreateContestOccurrenceFormProps) {
  const t = useTranslations('pages.contest.occurrences.create-occurrence');

  const schema = useMemo(() => createContestOccurrenceFormSchema(t), [t]);
  const { mutate: createOccurrence, isPending } = useCreateContestOccurrence();

  const form = useForm<CreateContestOccurrenceFormValues>({
    resolver: zodResolver(schema),
    defaultValues: {
      title: '',
      description: '',
    },
  });

  const [open, setOpen] = useState(false);

  const formSubmitHandler = (values: CreateContestOccurrenceFormValues) => {
    createOccurrence(
      {
        contestId,
        data: {
          title: values.title,
          description: values.description,
        },
      },
      {
        onSuccess: () => {
          toast.success(t('messages.success'));
          form.reset();
          setOpen(false);
        },
        onError: () => {
          toast.error(t('messages.error'));
        },
      },
    );
  };

  return (
    <Dialog open={open} onOpenChange={setOpen}>
      <DialogTrigger asChild>
        <Button>
          <PlusIcon />
          {t('label')}
        </Button>
      </DialogTrigger>
      <DialogContent>
        <DialogHeader>
          <DialogTitle>{t('title')}</DialogTitle>
          <DialogDescription>{t('description')}</DialogDescription>
        </DialogHeader>

        <form onSubmit={form.handleSubmit(formSubmitHandler)}>
          <FieldGroup>
            <Controller
              name="title"
              control={form.control}
              render={({ field, fieldState }) => (
                <Field>
                  <div className="flex items-center justify-between">
                    <FieldLabel>{t('form.title.label')}</FieldLabel>
                    <FieldError errors={[fieldState.error]} />
                  </div>
                  <Input
                    {...field}
                    aria-invalid={fieldState.invalid}
                    placeholder={t('form.title.placeholder')}
                  />
                </Field>
              )}
            />

            <Controller
              name="description"
              control={form.control}
              render={({ field, fieldState }) => (
                <Field>
                  <div className="flex items-center justify-between">
                    <FieldLabel>{t('form.description.label')}</FieldLabel>
                    <FieldError errors={[fieldState.error]} />
                  </div>
                  <Textarea
                    {...field}
                    aria-invalid={fieldState.invalid}
                    placeholder={t('form.description.placeholder')}
                    rows={5}
                  />
                </Field>
              )}
            />

            <DialogFooter>
              <Button type="submit" disabled={isPending}>
                {t('form.submit.label')}
              </Button>
            </DialogFooter>
          </FieldGroup>
        </form>
      </DialogContent>
    </Dialog>
  );
}
