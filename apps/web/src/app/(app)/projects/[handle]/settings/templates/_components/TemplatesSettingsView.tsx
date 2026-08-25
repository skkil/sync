'use client';

import { PencilIcon, PlusIcon, TrashIcon } from '@phosphor-icons/react';
import { useQueryClient } from '@tanstack/react-query';
import { useTranslations } from 'next-intl';
import { useParams } from 'next/navigation';
import { toast } from 'sonner';

import {
  getGetProjectPostTemplatesQueryKey,
  useDeleteProjectPostTemplate,
  useGetProjectPostTemplates,
} from '@/api/__generated__/post-template/post-template';
import { useGetProjectByHandle } from '@/api/__generated__/project/project';
import {
  GetPostTemplatesResponseTemplatesItem,
  GetProjectResponseRole,
} from '@/api/__generated__/types';
import {
  AlertDialog,
  AlertDialogAction,
  AlertDialogCancel,
  AlertDialogContent,
  AlertDialogDescription,
  AlertDialogFooter,
  AlertDialogHeader,
  AlertDialogTitle,
  AlertDialogTrigger,
} from '@/components/ui/alert-dialog';
import { Button, LinkButton } from '@/components/ui/button';
import {
  Table,
  TableBody,
  TableCell,
  TableHead,
  TableHeader,
  TableRow,
} from '@/components/ui/table';
import ROUTES from '@/util/routes';

export default function TemplatesSettingsView() {
  const t = useTranslations('pages.projects.project.settings.templates');

  const { handle } = useParams<{ handle: string }>();
  const { data: projectData } = useGetProjectByHandle(handle);
  const isAdmin = projectData?.data.role === GetProjectResponseRole.Admin;

  const { data, isPending } = useGetProjectPostTemplates(handle);
  const templates = data?.data.templates ?? [];

  return (
    <div className="space-y-7">
      <div className="flex items-start justify-between gap-4">
        <div>
          <h2 className="text-lg font-semibold">{t('heading')}</h2>
          <p className="text-sm text-muted-foreground">{t('description')}</p>
        </div>
        {isAdmin && (
          <LinkButton
            size="sm"
            href={ROUTES.PROJECT_SETTINGS_TEMPLATES_NEW(handle)}
          >
            <PlusIcon className="h-4 w-4" />
            {t('create-link')}
          </LinkButton>
        )}
      </div>

      {isPending ? (
        <TemplatesTableSkeleton />
      ) : templates.length === 0 ? (
        <div className="rounded-md border px-4 py-8 text-center">
          <p className="text-sm text-muted-foreground">{t('empty')}</p>
        </div>
      ) : (
        <Table className="border-separate border-spacing-y-1">
          <TableHeader>
            <TableRow className="hover:bg-transparent">
              <TableHead className="border-l-0">
                {t('table.columns.name')}
              </TableHead>
              <TableHead className="border-l-0">
                {t('table.columns.title')}
              </TableHead>
              <TableHead className="w-0 border-l-0" />
            </TableRow>
          </TableHeader>
          <TableBody>
            {templates.map((template) => (
              <TableRow key={template.externalId} className="border-0">
                <TableCell className="border-l-0 font-medium">
                  {template.name}
                </TableCell>
                <TableCell className="border-l-0 text-muted-foreground">
                  {template.title || t('table.no-title')}
                </TableCell>
                <TableCell className="border-l-0">
                  {isAdmin && (
                    <div className="flex justify-end gap-2">
                      <LinkButton
                        variant="outline"
                        size="sm"
                        href={ROUTES.PROJECT_SETTINGS_TEMPLATE_EDIT(
                          handle,
                          template.externalId,
                        )}
                      >
                        <PencilIcon className="h-4 w-4" />
                        {t('edit-link')}
                      </LinkButton>
                      <DeleteTemplateButton template={template} />
                    </div>
                  )}
                </TableCell>
              </TableRow>
            ))}
          </TableBody>
        </Table>
      )}
    </div>
  );
}

function DeleteTemplateButton({
  template,
}: {
  template: GetPostTemplatesResponseTemplatesItem;
}) {
  const t = useTranslations('pages.projects.project.settings.templates.delete');

  const { handle } = useParams<{ handle: string }>();
  const queryClient = useQueryClient();

  const { mutate: deleteTemplate, isPending } = useDeleteProjectPostTemplate();

  const onDelete = () => {
    deleteTemplate(
      { handle, externalId: template.externalId },
      {
        onSuccess: async () => {
          toast.success(t('messages.success'));
          await queryClient.invalidateQueries({
            queryKey: getGetProjectPostTemplatesQueryKey(handle),
          });
        },
        onError: () => {
          toast.error(t('messages.error'));
        },
      },
    );
  };

  return (
    <AlertDialog>
      <AlertDialogTrigger asChild>
        <Button
          variant="outline"
          size="sm"
          disabled={isPending}
          className="border-destructive/50 text-destructive hover:bg-destructive/10"
        >
          <TrashIcon className="h-4 w-4" />
          {t('trigger')}
        </Button>
      </AlertDialogTrigger>
      <AlertDialogContent>
        <AlertDialogHeader>
          <AlertDialogTitle>{t('title')}</AlertDialogTitle>
          <AlertDialogDescription>
            {t('description', { name: template.name })}
          </AlertDialogDescription>
        </AlertDialogHeader>
        <AlertDialogFooter>
          <AlertDialogCancel>{t('cancel')}</AlertDialogCancel>
          <AlertDialogAction
            variant="destructive"
            onClick={(event) => {
              event.preventDefault();
              onDelete();
            }}
          >
            {t('submit')}
          </AlertDialogAction>
        </AlertDialogFooter>
      </AlertDialogContent>
    </AlertDialog>
  );
}

function TemplatesTableSkeleton() {
  return (
    <div className="space-y-2">
      {Array.from({ length: 3 }).map((_, i) => (
        <div key={i} className="h-10 w-full rounded bg-muted animate-pulse" />
      ))}
    </div>
  );
}
