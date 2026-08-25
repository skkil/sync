'use client';

import { zodResolver } from '@hookform/resolvers/zod';
import { useQueryClient } from '@tanstack/react-query';
import { useTranslations } from 'next-intl';
import { useParams, useRouter } from 'next/navigation';
import { useEffect, useMemo, useRef } from 'react';
import { useForm } from 'react-hook-form';
import { toast } from 'sonner';
import z from 'zod';

import {
  getGetProjectPostTemplatesQueryKey,
  useCreateProjectPostTemplate,
  useGetProjectPostTemplates,
  useUpdateProjectPostTemplate,
} from '@/api/__generated__/post-template/post-template';
import {
  TemplateContentEditor,
  type TemplateContentEditorHandle,
} from '@/components/feature/post/editor/TemplateContentEditor';
import { Button, LinkButton } from '@/components/ui/button';
import {
  Field,
  FieldDescription,
  FieldError,
  FieldGroup,
  FieldLabel,
} from '@/components/ui/field';
import { Input } from '@/components/ui/input';
import SyncError, { ErrorCode } from '@/lib/error';
import ROUTES from '@/util/routes';

/** 서버 제한(PostConstants.MAX_TEMPLATE_CONTENT_LENGTH)과 같은 값. */
const TEMPLATE_CONTENT_MAX_LENGTH = 100_000;

interface TemplateFormValues {
  name: string;
  title: string;
}

interface TemplateEditorViewProps {
  /** 수정할 템플릿의 외부 식별자. 없으면 새 템플릿 작성이다. */
  externalId?: string;
}

export default function TemplateEditorView({
  externalId,
}: TemplateEditorViewProps) {
  const t = useTranslations('pages.projects.project.settings.templates.form');

  const { handle } = useParams<{ handle: string }>();
  const router = useRouter();
  const queryClient = useQueryClient();
  const isEditing = Boolean(externalId);

  const { data, isPending: isListPending } = useGetProjectPostTemplates(handle);
  const template = externalId
    ? data?.data.templates?.find((item) => item.externalId === externalId)
    : undefined;

  const editorRef = useRef<TemplateContentEditorHandle>(null);

  const formSchema = useMemo(
    () =>
      z.object({
        name: z
          .string()
          .trim()
          .min(1, t('errors.name-required'))
          .max(255, t('errors.name-too-long')),
        // 제목 접두어는 "트러블슈팅: " 처럼 끝 공백이 의미를 가지므로 trim 하지 않는다.
        title: z.string().max(255, t('errors.title-too-long')),
      }),
    [t],
  );

  const form = useForm<TemplateFormValues>({
    resolver: zodResolver(formSchema),
    defaultValues: { name: '', title: '' },
  });

  useEffect(() => {
    if (template) {
      form.reset({ name: template.name, title: template.title ?? '' });
    }
  }, [template, form]);

  const { mutate: createTemplate, isPending: isCreating } =
    useCreateProjectPostTemplate();
  const { mutate: updateTemplate, isPending: isUpdating } =
    useUpdateProjectPostTemplate();
  const isSubmitting = isCreating || isUpdating;

  // form.handleSubmit 을 렌더 중에 호출해 두면 ref 를 읽는 클로저가 렌더에 노출되므로
  // (react-hooks/refs), 제출 이벤트 안에서 감싼다.
  const submitTemplate = (values: TemplateFormValues) => {
    const editor = editorRef.current;
    const content = editor && !editor.isEmpty() ? editor.getContent() : null;
    if (!content) {
      toast.error(t('messages.empty-content'));
      return;
    }
    if (content.json.length > TEMPLATE_CONTENT_MAX_LENGTH) {
      toast.error(t('messages.content-too-long'));
      return;
    }

    const payload = {
      name: values.name.trim(),
      title: values.title.trim() ? values.title : undefined,
      content: content.json,
    };

    const handleError = (error: Error) => {
      if (error instanceof SyncError) {
        switch (error.code) {
          case ErrorCode.POST_TEMPLATE_ALREADY_EXISTS:
            toast.error(t('messages.already-exists'));
            return;
          case ErrorCode.POST_TEMPLATE_LIMIT_EXCEEDED:
            toast.error(t('messages.limit-exceeded'));
            return;
          case ErrorCode.POST_TEMPLATE_NOT_FOUND:
            toast.error(t('messages.not-found'));
            return;
        }
      }
      toast.error(t('messages.error'));
    };

    const onSuccess = async () => {
      toast.success(
        isEditing ? t('messages.update-success') : t('messages.create-success'),
      );
      await queryClient.invalidateQueries({
        queryKey: getGetProjectPostTemplatesQueryKey(handle),
      });
      router.push(ROUTES.PROJECT_SETTINGS_TEMPLATES(handle));
    };

    if (externalId) {
      updateTemplate(
        { handle, externalId, data: payload },
        { onSuccess, onError: handleError },
      );
    } else {
      createTemplate(
        { handle, data: payload },
        { onSuccess, onError: handleError },
      );
    }
  };

  if (isEditing && isListPending) {
    return <TemplateFormSkeleton />;
  }

  if (isEditing && !template) {
    return (
      <div className="space-y-4 rounded-md border px-4 py-8 text-center">
        <p className="text-sm text-muted-foreground">{t('not-found')}</p>
        <LinkButton
          variant="outline"
          size="sm"
          href={ROUTES.PROJECT_SETTINGS_TEMPLATES(handle)}
        >
          {t('back')}
        </LinkButton>
      </div>
    );
  }

  return (
    <div className="space-y-6">
      <h2 className="text-lg font-semibold">
        {isEditing ? t('heading-edit') : t('heading-new')}
      </h2>

      <form
        onSubmit={(event) => {
          void form.handleSubmit(submitTemplate)(event);
        }}
        className="space-y-6"
      >
        <FieldGroup>
          <Field>
            <FieldLabel>{t('name-label')}</FieldLabel>
            <Input
              {...form.register('name')}
              placeholder={t('name-placeholder')}
            />
            {form.formState.errors.name?.message && (
              <FieldError>{form.formState.errors.name.message}</FieldError>
            )}
          </Field>

          <Field>
            <FieldLabel>{t('title-label')}</FieldLabel>
            <Input
              {...form.register('title')}
              placeholder={t('title-placeholder')}
            />
            <FieldDescription>{t('title-help')}</FieldDescription>
            {form.formState.errors.title?.message && (
              <FieldError>{form.formState.errors.title.message}</FieldError>
            )}
          </Field>

          <Field>
            <FieldLabel>{t('content-label')}</FieldLabel>
            <TemplateContentEditor
              ref={editorRef}
              initialContent={template?.content}
              placeholder={t('content-placeholder')}
            />
          </Field>
        </FieldGroup>

        <div className="flex justify-end gap-2">
          <LinkButton
            variant="outline"
            href={ROUTES.PROJECT_SETTINGS_TEMPLATES(handle)}
          >
            {t('cancel')}
          </LinkButton>
          <Button type="submit" disabled={isSubmitting}>
            {isEditing ? t('submit-update') : t('submit-create')}
          </Button>
        </div>
      </form>
    </div>
  );
}

function TemplateFormSkeleton() {
  return (
    <div className="space-y-4">
      <div className="h-8 w-40 rounded bg-muted animate-pulse" />
      <div className="h-10 w-full rounded bg-muted animate-pulse" />
      <div className="h-10 w-full rounded bg-muted animate-pulse" />
      <div className="h-60 w-full rounded bg-muted animate-pulse" />
    </div>
  );
}
