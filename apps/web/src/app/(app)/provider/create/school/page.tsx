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
import { Field, FieldGroup, FieldLabel } from '@/components/ui/field';
import { Input } from '@/components/ui/input';
import {
  Select,
  SelectContent,
  SelectItem,
  SelectTrigger,
  SelectValue,
} from '@/components/ui/select';
import { useCreateProviderMutation } from '@/features/provider/api/create-provider';
import { ProviderType, SchoolType } from '@/types/provider';

export default function CreateProvider() {
  const t = useTranslations('pages.create-school');

  const router = useRouter();
  const { mutate: createProvider } = useCreateProviderMutation();

  const CreateProviderFormSchema = z.object({
    name: z.string().min(1, {
      error: t('errors.required_name'),
    }),
    description: z.string().min(1, {
      error: t('errors.required_description'),
    }),
    schoolType: z.enum(SchoolType).nonoptional({
      error: t('errors.required_school_type'),
    }),
  });
  type CreateProviderFormValues = z.infer<typeof CreateProviderFormSchema>;

  const form = useForm<CreateProviderFormValues>({
    resolver: zodResolver(CreateProviderFormSchema),
    defaultValues: {
      name: '',
      description: '',
    },
  });

  const formSubmitHandler = async (values: CreateProviderFormValues) => {
    createProvider(
      {
        type: ProviderType.SCHOOL,
        name: values.name,
        description: values.description,
        schoolType: values.schoolType,
      },
      {
        onSuccess: (data) => {
          router.push(`/school/${data.id}`);
        },
      },
    );
  };

  return (
    <form onSubmit={form.handleSubmit(formSubmitHandler)}>
      <Card className="max-w-3xl mx-auto">
        <CardHeader>
          <Button variant="ghost" size="icon" onClick={() => router.back()}>
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
                    <FieldLabel>{t('form.name.label')}</FieldLabel>
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
                    <FieldLabel>{t('form.description.label')}</FieldLabel>
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
              name="schoolType"
              control={form.control}
              render={({ field, fieldState }) => {
                return (
                  <Field>
                    <FieldLabel>{t('form.school-type.label')}</FieldLabel>
                    <Select
                      onValueChange={field.onChange}
                      defaultValue={field.value}
                      value={field.value}
                      aria-invalid={fieldState.invalid}
                    >
                      <SelectTrigger>
                        <SelectValue
                          placeholder={t('form.school-type.placeholder')}
                        />
                      </SelectTrigger>
                      <SelectContent>
                        {Object.values(SchoolType).map((type) => (
                          <SelectItem key={type} value={type}>
                            {t(`form.school-type.types.${type}`)}
                          </SelectItem>
                        ))}
                      </SelectContent>
                    </Select>
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
