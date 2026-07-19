'use client';

import { zodResolver } from '@hookform/resolvers/zod';
import { UserPlusIcon } from '@phosphor-icons/react';
import { useTranslations } from 'next-intl';
import React, { useState } from 'react';
import { useForm } from 'react-hook-form';
import { toast } from 'sonner';
import z from 'zod';

import { useGetProjectByHandle } from '@/api/__generated__/project/project';
import {
  CreateProjectInvitationRequestRole,
  GetProjectResponseRole,
} from '@/api/__generated__/types';
import { useCreateProjectInvitation } from '@/components/feature/project/hooks/useProjectInvitation';
import { Button } from '@/components/ui/button';
import { Field, FieldGroup, FieldLabel } from '@/components/ui/field';
import { Input } from '@/components/ui/input';
import {
  Popover,
  PopoverContent,
  PopoverHeader,
  PopoverTitle,
  PopoverTrigger,
} from '@/components/ui/popover';

const AddTeammateFormSchema = z.object({
  handle: z
    .string()
    .trim()
    .transform((handle) => handle.replace(/^@/, ''))
    .pipe(z.string().min(1)),
});

type AddTeammateFormValues = z.infer<typeof AddTeammateFormSchema>;

interface AddTeammatePopoverProps {
  projectHandle: string;
  trigger?: React.ReactNode;
}

export default function AddTeammatePopover({
  projectHandle,
  trigger,
}: AddTeammatePopoverProps) {
  const t = useTranslations('pages.projects.project.overview.add-teammate');
  const [open, setOpen] = useState(false);
  const { data: projectData } = useGetProjectByHandle(projectHandle);

  const form = useForm<AddTeammateFormValues>({
    resolver: zodResolver(AddTeammateFormSchema),
    defaultValues: { handle: '' },
  });

  const { mutate: createInvitation, isPending } = useCreateProjectInvitation();

  const onSubmit = form.handleSubmit((values) => {
    createInvitation(
      {
        handle: projectHandle,
        data: {
          inviteeHandle: values.handle,
          role: CreateProjectInvitationRequestRole.Member,
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
  });

  if (projectData?.data.role !== GetProjectResponseRole.Admin) {
    return null;
  }

  return (
    <Popover open={open} onOpenChange={setOpen}>
      <PopoverTrigger asChild>
        {trigger ?? (
          <Button variant="ghost" size="icon" aria-label={t('trigger')}>
            <UserPlusIcon className="h-4 w-4" />
          </Button>
        )}
      </PopoverTrigger>

      <PopoverContent align="start">
        <PopoverHeader>
          <PopoverTitle>{t('title')}</PopoverTitle>
        </PopoverHeader>

        <form onSubmit={onSubmit} className="flex flex-col gap-3">
          <FieldGroup>
            <Field>
              <FieldLabel>{t('fields.handle')}</FieldLabel>
              <Input
                {...form.register('handle')}
                placeholder={t('fields.handle-placeholder')}
              />
            </Field>
          </FieldGroup>

          <div className="flex justify-end gap-2">
            <Button
              type="button"
              variant="outline"
              size="sm"
              onClick={() => {
                form.reset();
                setOpen(false);
              }}
            >
              {t('actions.cancel')}
            </Button>
            <Button
              type="submit"
              size="sm"
              disabled={!form.formState.isDirty || isPending}
            >
              {t('actions.invite')}
            </Button>
          </div>
        </form>
      </PopoverContent>
    </Popover>
  );
}
