'use client';

import { zodResolver } from '@hookform/resolvers/zod';
import { CaretLeftIcon } from '@phosphor-icons/react';
import { useTranslations } from 'next-intl';
import { useRouter } from 'next/navigation';
import { Controller, useForm } from 'react-hook-form';
import z from 'zod';

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
import { useCreateProviderMutation } from '@/features/provider/api/create-provider';
import { ProviderType } from '@/types/provider';

export default function CreateCompany() {
  const t = useTranslations('pages.create-company');

  const router = useRouter();
  const { mutate: createProvider } = useCreateProviderMutation();

  const CreateProviderFormSchema = z.object({
    name: z.string().min(1, {
      error: t('errors.required_name'),
    }),
    description: z.string().min(1, {
      error: t('errors.required_description'),
    }),
    industry: z.string().min(1, {
      error: t('errors.required_industry'),
    }),
  });
  type CreateProviderFormValues = z.infer<typeof CreateProviderFormSchema>;

  const form = useForm<CreateProviderFormValues>({
    resolver: zodResolver(CreateProviderFormSchema),
    defaultValues: {
      name: '',
      description: '',
      industry: '',
    },
  });

  const formSubmitHandler = async (values: CreateProviderFormValues) => {
    createProvider(
      {
        type: ProviderType.COMPANY,
        name: values.name,
        description: values.description,
        industry: values.industry,
      },
      {
        onSuccess: (data) => {
          router.push(`/company/${data.id}`);
        },
      },
    );
  };

  return (
    <form onSubmit={form.handleSubmit(formSubmitHandler)}>
      <Card className="max-w-3xl mx-auto">
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
              render={({ field, fieldState }) => {
                return (
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
                );
              }}
            />

            <Controller
              name="description"
              control={form.control}
              render={({ field, fieldState }) => {
                return (
                  <Field>
                    <div className="flex items-center justify-between">
                      <FieldLabel>{t('form.description.label')}</FieldLabel>
                      <FieldError errors={[fieldState.error]} />
                    </div>
                    <Input
                      {...field}
                      aria-invalid={fieldState.invalid}
                      placeholder={t('form.description.placeholder')}
                    />
                  </Field>
                );
              }}
            />

            <Controller
              name="industry"
              control={form.control}
              render={({ field, fieldState }) => {
                return (
                  <Field>
                    <div className="flex items-center justify-between">
                      <FieldLabel>{t('form.industry.label')}</FieldLabel>
                      <FieldError errors={[fieldState.error]} />
                    </div>
                    <Input
                      {...field}
                      aria-invalid={fieldState.invalid}
                      placeholder={t('form.industry.placeholder')}
                    />
                  </Field>
                );
              }}
            />
          </FieldGroup>
        </CardContent>

        <CardFooter className="flex justify-end">
          <div>
            <Button type="submit">{t('form.submit.label')}</Button>
          </div>
        </CardFooter>
      </Card>
    </form>
  );
}
