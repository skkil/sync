'use client';

import { zodResolver } from '@hookform/resolvers/zod';
import { PlusIcon } from '@phosphor-icons/react';
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
import {
  Field,
  FieldError,
  FieldGroup,
  FieldLabel,
} from '@/components/ui/field';
import { Input } from '@/components/ui/input';
import { Textarea } from '@/components/ui/textarea';
import useCreateTeamBuildingPostMutation from '@/features/provider/api/project/create-team-building-post';

interface CreateTeamBuildingPostFormProps {
  projectId: string;
}

const createTeamBuildingPostFormSchema = (t: (key: string) => string) =>
  z.object({
    title: z.string().min(1, t('errors.required-title')),
    content: z.string(),
  });

type CreateTeamBuildingPostFormValues = z.infer<
  ReturnType<typeof createTeamBuildingPostFormSchema>
>;

export default function CreateTeamBuildingPostForm({
  projectId,
}: CreateTeamBuildingPostFormProps) {
  const t = useTranslations('pages.project.admin.team-building.form');

  const { mutate: createTeamBuildingPost } =
    useCreateTeamBuildingPostMutation(projectId);

  const schema = useMemo(() => createTeamBuildingPostFormSchema(t), [t]);

  const form = useForm<CreateTeamBuildingPostFormValues>({
    resolver: zodResolver(schema),
    defaultValues: {
      title: '',
      content: '',
    },
  });

  const [open, setOpen] = useState(false);

  const formSubmitHandler = (values: CreateTeamBuildingPostFormValues) => {
    createTeamBuildingPost(values, {
      onSuccess: () => {
        form.reset();
        setOpen(false);
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
          <DialogDescription>{t('form-description')}</DialogDescription>
        </DialogHeader>

        <form onSubmit={form.handleSubmit(formSubmitHandler)}>
          <FieldGroup>
            <Controller
              name="title"
              control={form.control}
              render={({ field, fieldState }) => (
                <Field>
                  <div className="flex items-center justify-between">
                    <FieldLabel>{t('post-title.label')}</FieldLabel>
                    <FieldError errors={[fieldState.error]} />
                  </div>
                  <Input
                    {...field}
                    aria-invalid={fieldState.invalid}
                    placeholder={t('post-title.placeholder')}
                  />
                </Field>
              )}
            />

            <Controller
              name="content"
              control={form.control}
              render={({ field, fieldState }) => (
                <Field>
                  <div className="flex items-center justify-between">
                    <FieldLabel>{t('content.label')}</FieldLabel>
                    <FieldError errors={[fieldState.error]} />
                  </div>
                  <Textarea
                    {...field}
                    aria-invalid={fieldState.invalid}
                    placeholder={t('content.placeholder')}
                    rows={5}
                  />
                </Field>
              )}
            />

            <DialogFooter>
              <Button type="submit">{t('submit.label')}</Button>
            </DialogFooter>
          </FieldGroup>
        </form>
      </DialogContent>
    </Dialog>
  );
}
