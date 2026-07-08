'use client';

import { zodResolver } from '@hookform/resolvers/zod';
import { UserPlusIcon } from '@phosphor-icons/react';
import { useTranslations } from 'next-intl';
import React, { useState } from 'react';
import { useForm } from 'react-hook-form';
import z from 'zod';

import {
  getGetProjectsByUserQueryOptions,
  useAddTeammate,
} from '@/api/__generated__/project/project';
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
  handle: z.string().min(1),
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

  const form = useForm<AddTeammateFormValues>({
    resolver: zodResolver(AddTeammateFormSchema),
    defaultValues: { handle: '' },
  });

  const { mutate: addTeammate } = useAddTeammate();

  const onSubmit = form.handleSubmit(async (values) => {
    addTeammate(
      {
        handle: projectHandle,
        data: {
          teammateHandle: values.handle,
        },
      },
      {
        onSuccess: async (_data, _variables, _onMutateResult, context) => {
          await context.client.invalidateQueries(
            getGetProjectsByUserQueryOptions(projectHandle),
          );

          form.reset();
          setOpen(false);
        },
      },
    );
  });

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
              disabled={!form.formState.isDirty || form.formState.isSubmitting}
            >
              {t('actions.invite')}
            </Button>
          </div>
        </form>
      </PopoverContent>
    </Popover>
  );
}
