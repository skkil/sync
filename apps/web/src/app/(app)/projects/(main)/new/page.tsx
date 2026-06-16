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
import { RadioGroup, RadioGroupItem } from '@/components/ui/radio-group';
import { Textarea } from '@/components/ui/textarea';
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
    description: z.string().optional(),
    isPublic: z.boolean(),
  });

  type CreateProjectFormValues = z.infer<typeof CreateProjectFormSchema>;

  const form = useForm<CreateProjectFormValues>({
    resolver: zodResolver(CreateProjectFormSchema),
    mode: 'onChange',
    defaultValues: {
      name: '',
      handle: '',
      description: '',
      isPublic: true,
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
      {
        data: {
          name: values.name,
          handle: values.handle,
          description: values.description || null,
          isPublic: values.isPublic,
        },
      },
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
                    />
                  </Field>
                )}
              />
              <Controller
                name="isPublic"
                control={form.control}
                render={({ field }) => (
                  <Field>
                    <FieldLabel>{t('form.visibility.label')}</FieldLabel>
                    <RadioGroup
                      value={field.value ? 'public' : 'private'}
                      onValueChange={(v) => field.onChange(v === 'public')}
                      className="mt-1"
                    >
                      <label className="flex cursor-pointer items-start gap-3 rounded-lg border p-3 has-[[data-state=checked]]:border-primary">
                        <RadioGroupItem value="public" className="mt-0.5" />
                        <div>
                          <p className="text-sm font-medium">
                            {t('form.visibility.public')}
                          </p>
                          <p className="text-muted-foreground text-xs">
                            {t('form.visibility.public_description')}
                          </p>
                        </div>
                      </label>
                      <label className="flex cursor-pointer items-start gap-3 rounded-lg border p-3 has-[[data-state=checked]]:border-primary">
                        <RadioGroupItem value="private" className="mt-0.5" />
                        <div>
                          <p className="text-sm font-medium">
                            {t('form.visibility.private')}
                          </p>
                          <p className="text-muted-foreground text-xs">
                            {t('form.visibility.private_description')}
                          </p>
                        </div>
                      </label>
                    </RadioGroup>
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
