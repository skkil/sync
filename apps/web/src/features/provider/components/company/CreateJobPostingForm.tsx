'use client';

import { zodResolver } from '@hookform/resolvers/zod';
import { PlusIcon } from '@phosphor-icons/react';
import { useTranslations } from 'next-intl';
import { useMemo, useState } from 'react';
import { Controller, useForm } from 'react-hook-form';
import { toast } from 'sonner';
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
import {
  Field,
  FieldError,
  FieldGroup,
  FieldLabel,
} from '@/components/ui/field';
import { Input } from '@/components/ui/input';
import { Textarea } from '@/components/ui/textarea';

import { useCreateJobPostingMutation } from '../../api/company/create-job-posting';

interface CreateJobFormProps {
  companyId: string;
}

const createJobFormSchema = (t: (key: string) => string) =>
  z.object({
    jobTitle: z.string().min(1, t('errors.required-job-title')),
    jobDescription: z.string().min(1, t('errors.required-job-description')),
    location: z.string().optional(),
  });

type CreateJobFormValues = z.infer<ReturnType<typeof createJobFormSchema>>;

export default function CreateJobPostingForm({
  companyId: providerId,
}: CreateJobFormProps) {
  const t = useTranslations('pages.company.job-postings.create-posting.form');

  const schema = useMemo(() => createJobFormSchema(t), [t]);
  const { mutate: createJob, isPending } =
    useCreateJobPostingMutation(providerId);

  const form = useForm<CreateJobFormValues>({
    resolver: zodResolver(schema),
    defaultValues: {
      jobTitle: '',
      jobDescription: '',
      location: '',
    },
  });

  const [open, setOpen] = useState(false);

  const formSubmitHandler = (values: CreateJobFormValues) => {
    createJob(values, {
      onSuccess: () => {
        toast.success(t('messages.success'));
        form.reset();
        setOpen(false);
      },
      onError: () => {
        toast.error(t('messages.error'));
      },
    });
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
              name="jobTitle"
              control={form.control}
              render={({ field, fieldState }) => (
                <Field>
                  <div className="flex items-center justify-between">
                    <FieldLabel>{t('job-title.label')}</FieldLabel>
                    <FieldError errors={[fieldState.error]} />
                  </div>
                  <Input
                    {...field}
                    aria-invalid={fieldState.invalid}
                    placeholder={t('job-title.placeholder')}
                  />
                </Field>
              )}
            />

            <Controller
              name="jobDescription"
              control={form.control}
              render={({ field, fieldState }) => (
                <Field>
                  <div className="flex items-center justify-between">
                    <FieldLabel>{t('job-description.label')}</FieldLabel>
                    <FieldError errors={[fieldState.error]} />
                  </div>
                  <Textarea
                    {...field}
                    aria-invalid={fieldState.invalid}
                    placeholder={t('job-description.placeholder')}
                    rows={5}
                  />
                </Field>
              )}
            />

            <Controller
              name="location"
              control={form.control}
              render={({ field, fieldState }) => (
                <Field>
                  <div className="flex items-center justify-between">
                    <FieldLabel>{t('location.label')}</FieldLabel>
                    <FieldError errors={[fieldState.error]} />
                  </div>
                  <Input
                    {...field}
                    aria-invalid={fieldState.invalid}
                    placeholder={t('location.placeholder')}
                  />
                </Field>
              )}
            />

            <DialogFooter>
              <Button type="submit" disabled={isPending}>
                {t('submit.label')}
              </Button>
            </DialogFooter>
          </FieldGroup>
        </form>
      </DialogContent>
    </Dialog>
  );
}
