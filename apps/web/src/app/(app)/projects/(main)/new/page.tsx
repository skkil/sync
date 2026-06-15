'use client';

import { zodResolver } from '@hookform/resolvers/zod';
import { CaretLeftIcon } from '@phosphor-icons/react';
import { useDebounce } from '@uidotdev/usehooks';
import { useTranslations } from 'next-intl';
import { redirect, useRouter } from 'next/navigation';
import { useEffect } from 'react';
import { Controller, useForm } from 'react-hook-form';
import { toast } from 'sonner';
import z from 'zod';

import {
  useCreateProject,
  useGetProjectHandleAvailability,
} from '@/api/__generated__/project/project';
import { Button } from '@/components/ui/button';
import {
  Card,
  CardContent,
  CardFooter,
  CardHeader,
  CardTitle,
} from '@/components/ui/card';
import {
  Field,
  FieldError,
  FieldGroup,
  FieldLabel,
} from '@/components/ui/field';
import { Input } from '@/components/ui/input';
import { isAuthenticated } from '@/lib/auth';
import { useSession } from '@/lib/auth/client';

export default function CreateProjectPage() {
  const t = useTranslations('pages.projects.new');
  const router = useRouter();
  const { data: session, isPending: isSessionPending } = useSession();

  const { mutate: createProject, isPending } = useCreateProject();

  const CreateProjectFormSchema = z.object({
    name: z.string().min(1, {
      message: t('form.errors.required_name'),
    }),
    handle: z
      .string()
      .min(6, { message: t('form.errors.handle_min_length', { length: 6 }) })
      .max(30, { message: t('form.errors.handle_max_length', { length: 30 }) })
      .regex(/^[a-zA-Z0-9_-]+$/, {
        message: t('form.errors.handle_invalid_characters'),
      }),
  });

  type CreateProjectFormValues = z.infer<typeof CreateProjectFormSchema>;

  const form = useForm<CreateProjectFormValues>({
    resolver: zodResolver(CreateProjectFormSchema),
    mode: 'onChange',
    defaultValues: {
      name: '',
      handle: '',
    },
  });

  // eslint-disable-next-line react-hooks/incompatible-library
  const handle = form.watch('handle');
  const debouncedHandle = useDebounce(handle, 500);

  const HANDLE_REGEX = /^[a-zA-Z0-9_]+$/;
  const isHandleQueryable =
    debouncedHandle.length >= 6 && HANDLE_REGEX.test(debouncedHandle);

  const { data: handleAvailability, isFetching: isCheckingHandle } =
    useGetProjectHandleAvailability(
      { handle: debouncedHandle },
      { query: { enabled: isHandleQueryable } },
    );

  useEffect(() => {
    if (!isHandleQueryable) return;

    if (handleAvailability && !handleAvailability.data.available) {
      form.setError('handle', { message: t('form.errors.handle_in_use') });
    } else if (handleAvailability?.data.available) {
      form.clearErrors('handle');
    }
  }, [handleAvailability, isHandleQueryable, form, t]);

  const isAvailabilityPending = isCheckingHandle || handle !== debouncedHandle;

  const formSubmitHandler = async (values: CreateProjectFormValues) => {
    createProject(
      { data: { name: values.name, handle: values.handle } },
      {
        onSuccess: ({ data: { handle } }) => {
          toast.success(t('form.submit.success'));
          router.push(`/projects/${handle}`);
        },
        onError: () => {
          toast.error(t('form.submit.error'));
        },
      },
    );
  };

  if (isSessionPending) {
    return null;
  }

  if (!isAuthenticated(session)) {
    redirect('/auth/login');
  }

  return (
    <div>
      <form onSubmit={form.handleSubmit(formSubmitHandler)}>
        <Card>
          <CardHeader>
            <Button
              type="button"
              variant="ghost"
              size="icon"
              onClick={() => router.back()}
            >
              <CaretLeftIcon />
            </Button>

            <CardTitle className="text-lg">{t('title')}</CardTitle>
          </CardHeader>

          <CardContent>
            <FieldGroup>
              <Controller
                name="name"
                control={form.control}
                render={({ field, fieldState }) => (
                  <Field>
                    <div className="flex items-center justify-between">
                      <FieldLabel>{t('form.name.label')}</FieldLabel>
                      <FieldError errors={[fieldState.error]} />
                    </div>
                    <Input
                      {...field}
                      aria-invalid={fieldState.invalid}
                      placeholder={t('form.name.placeholder')}
                    />
                  </Field>
                )}
              />
              <Controller
                name="handle"
                control={form.control}
                render={({ field, fieldState }) => (
                  <Field>
                    <div className="flex items-center justify-between">
                      <FieldLabel>{t('form.handle.label')}</FieldLabel>
                      <FieldError errors={[fieldState.error]} />
                    </div>
                    <Input
                      {...field}
                      aria-invalid={fieldState.invalid}
                      placeholder={t('form.handle.placeholder')}
                    />
                  </Field>
                )}
              />
            </FieldGroup>
          </CardContent>

          <CardFooter className="flex justify-end">
            <Button
              type="submit"
              disabled={
                isPending ||
                isAvailabilityPending ||
                handleAvailability?.data.available === false
              }
            >
              {isPending ? t('form.submit.creating') : t('form.submit.label')}
            </Button>
          </CardFooter>
        </Card>
      </form>
    </div>
  );
}
