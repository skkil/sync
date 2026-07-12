'use client';

import { zodResolver } from '@hookform/resolvers/zod';
import { CheckIcon, PlusIcon } from '@phosphor-icons/react';
import { useQueryClient } from '@tanstack/react-query';
import { useTranslations } from 'next-intl';
import { useParams } from 'next/navigation';
import { useState } from 'react';
import { useForm } from 'react-hook-form';
import { toast } from 'sonner';
import z from 'zod';

import {
  getGetProjectTagsQueryKey,
  getGetProjectUnverifiedTagsQueryKey,
  useCreateProjectTag,
  useGetProjectTags,
  useGetProjectUnverifiedTags,
  useVerifyTag,
} from '@/api/__generated__/tag/tag';
import { GetTagsResponseTagsItem } from '@/api/__generated__/types';
import { Badge } from '@/components/ui/badge';
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
import {
  Table,
  TableBody,
  TableCell,
  TableHead,
  TableHeader,
  TableRow,
} from '@/components/ui/table';

export default function ManageProjectTags() {
  return (
    <div className="space-y-8">
      <div className="flex justify-end">
        <CreateProjectTagPopover />
      </div>

      <UnverifiedTagsSection />
      <VerifiedTagsSection />
    </div>
  );
}

const CreateProjectTagFormSchema = z.object({
  name: z.string().trim().min(1),
});

type CreateProjectTagFormValues = z.infer<typeof CreateProjectTagFormSchema>;

function CreateProjectTagPopover() {
  const t = useTranslations('pages.projects.project.tags.manage.create');

  const { handle } = useParams<{ handle: string }>();
  const queryClient = useQueryClient();
  const [open, setOpen] = useState(false);

  const form = useForm<CreateProjectTagFormValues>({
    resolver: zodResolver(CreateProjectTagFormSchema),
    defaultValues: { name: '' },
  });

  const { mutate: createProjectTag, isPending } = useCreateProjectTag();

  const onSubmit = form.handleSubmit((values) => {
    createProjectTag(
      { handle, data: { name: values.name } },
      {
        onSuccess: async (response) => {
          toast.success(t('messages.success', { name: response.data.name }));
          await queryClient.invalidateQueries({
            queryKey: getGetProjectTagsQueryKey(handle),
          });
          form.reset();
          setOpen(false);
        },
        onError: () => {
          toast.error(t('messages.error'));
        },
      },
    );
  });

  return (
    <Popover open={open} onOpenChange={setOpen}>
      <PopoverTrigger asChild>
        <Button size="sm">
          <PlusIcon className="h-4 w-4" />
          {t('trigger')}
        </Button>
      </PopoverTrigger>

      <PopoverContent align="end">
        <PopoverHeader>
          <PopoverTitle>{t('title')}</PopoverTitle>
        </PopoverHeader>

        <form onSubmit={onSubmit} className="flex flex-col gap-3">
          <FieldGroup>
            <Field>
              <FieldLabel>{t('name-label')}</FieldLabel>
              <Input
                {...form.register('name')}
                placeholder={t('name-placeholder')}
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
              {t('cancel')}
            </Button>
            <Button
              type="submit"
              size="sm"
              disabled={!form.formState.isDirty || isPending}
            >
              {t('submit')}
            </Button>
          </div>
        </form>
      </PopoverContent>
    </Popover>
  );
}

function UnverifiedTagsSection() {
  const t = useTranslations('pages.projects.project.tags.manage');

  const { handle } = useParams<{ handle: string }>();
  const queryClient = useQueryClient();

  const { data, isPending } = useGetProjectUnverifiedTags(handle);
  const tags = data?.data.tags ?? [];

  const { mutate: verifyTag, isPending: isVerifyPending } = useVerifyTag();

  const onVerify = (tag: GetTagsResponseTagsItem) => {
    verifyTag(
      { tagId: tag.id.toString() },
      {
        onSuccess: async () => {
          toast.success(t('unverified.messages.success', { name: tag.name }));
          await queryClient.invalidateQueries({
            queryKey: getGetProjectUnverifiedTagsQueryKey(handle),
          });
          await queryClient.invalidateQueries({
            queryKey: getGetProjectTagsQueryKey(handle),
          });
        },
        onError: () => {
          toast.error(t('unverified.messages.error'));
        },
      },
    );
  };

  return (
    <section className="space-y-3">
      <div>
        <h2 className="text-base font-semibold">{t('unverified.heading')}</h2>
        <p className="text-sm text-muted-foreground">
          {t('unverified.description')}
        </p>
      </div>

      {isPending ? (
        <TagsTableSkeleton />
      ) : tags.length === 0 ? (
        <div className="rounded-md border px-4 py-8 text-center">
          <p className="text-sm text-muted-foreground">
            {t('unverified.empty')}
          </p>
        </div>
      ) : (
        <Table className="border-separate border-spacing-y-1">
          <TableHeader>
            <TableRow className="hover:bg-transparent">
              <TableHead className="border-l-0">
                {t('table.columns.name')}
              </TableHead>
              <TableHead className="border-l-0">
                {t('table.columns.description')}
              </TableHead>
              <TableHead className="border-l-0">
                {t('table.columns.post-count')}
              </TableHead>
              <TableHead className="w-0 border-l-0" />
            </TableRow>
          </TableHeader>
          <TableBody>
            {tags.map((tag) => (
              <TableRow key={tag.id} className="border-0">
                <TableCell className="border-l-0 font-medium">
                  {tag.name}
                </TableCell>
                <TableCell className="border-l-0 text-muted-foreground">
                  {tag.description || t('table.no-description')}
                </TableCell>
                <TableCell className="border-l-0">{tag.postCount}</TableCell>
                <TableCell className="border-l-0">
                  <div className="flex justify-end">
                    <Button
                      variant="outline"
                      size="sm"
                      disabled={isVerifyPending}
                      onClick={() => onVerify(tag)}
                    >
                      <CheckIcon className="h-4 w-4" />
                      {t('unverified.verify')}
                    </Button>
                  </div>
                </TableCell>
              </TableRow>
            ))}
          </TableBody>
        </Table>
      )}
    </section>
  );
}

function VerifiedTagsSection() {
  const t = useTranslations('pages.projects.project.tags.manage');

  const { handle } = useParams<{ handle: string }>();
  const { data, isPending } = useGetProjectTags(handle);
  const tags = data?.data.tags ?? [];

  return (
    <section className="space-y-3">
      <div>
        <h2 className="text-base font-semibold">{t('verified.heading')}</h2>
        <p className="text-sm text-muted-foreground">
          {t('verified.description')}
        </p>
      </div>

      {isPending ? (
        <TagsTableSkeleton />
      ) : tags.length === 0 ? (
        <div className="rounded-md border px-4 py-8 text-center">
          <p className="text-sm text-muted-foreground">{t('verified.empty')}</p>
        </div>
      ) : (
        <Table className="border-separate border-spacing-y-1">
          <TableHeader>
            <TableRow className="hover:bg-transparent">
              <TableHead className="border-l-0">
                {t('table.columns.name')}
              </TableHead>
              <TableHead className="border-l-0">
                {t('table.columns.description')}
              </TableHead>
              <TableHead className="border-l-0">
                {t('table.columns.post-count')}
              </TableHead>
            </TableRow>
          </TableHeader>
          <TableBody>
            {tags.map((tag) => (
              <TableRow key={tag.id} className="border-0">
                <TableCell className="border-l-0 font-medium">
                  <div className="flex items-center gap-2">
                    {tag.name}
                    <Badge variant="secondary">{t('verified.badge')}</Badge>
                  </div>
                </TableCell>
                <TableCell className="border-l-0 text-muted-foreground">
                  {tag.description || t('table.no-description')}
                </TableCell>
                <TableCell className="border-l-0">{tag.postCount}</TableCell>
              </TableRow>
            ))}
          </TableBody>
        </Table>
      )}
    </section>
  );
}

function TagsTableSkeleton() {
  return (
    <div className="space-y-2">
      {Array.from({ length: 3 }).map((_, i) => (
        <div key={i} className="h-10 w-full rounded bg-muted animate-pulse" />
      ))}
    </div>
  );
}
