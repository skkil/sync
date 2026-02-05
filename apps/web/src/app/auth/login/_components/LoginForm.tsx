'use client';

import { zodResolver } from '@hookform/resolvers/zod';
import { useTranslations } from 'next-intl';
import Link from 'next/link';
import { useRouter } from 'next/navigation';
import { Controller, useForm } from 'react-hook-form';
import * as z from 'zod';

import { Button } from '@/components/ui/button';
import {
  Field,
  FieldError,
  FieldGroup,
  FieldLabel,
} from '@/components/ui/field';
import { Input } from '@/components/ui/input';
import { useLoginMutation } from '@/features/auth/api/login';

export default function LoginForm() {
  const t = useTranslations('pages.login.form');

  const router = useRouter();
  const { mutate: login } = useLoginMutation();

  const LoginFormSchema = z.object({
    email: z
      .email({
        error: t('errors.invalid_credentials'),
      })
      .nonoptional({
        error: t('errors.required_email'),
      }),
    password: z.string().nonempty({
      error: t('errors.required_password'),
    }),
  });

  type LoginFormValues = z.infer<typeof LoginFormSchema>;

  const form = useForm<LoginFormValues>({
    resolver: zodResolver(LoginFormSchema),
    defaultValues: {
      email: '',
      password: '',
    },
  });

  const onFormSubmit = async (values: LoginFormValues) => {
    login(
      {
        email: values.email,
        password: values.password,
      },
      {
        onSuccess: () => {
          router.push('/');
        },
      },
    );
  };

  return (
    <div>
      <form onSubmit={form.handleSubmit(onFormSubmit)}>
        <FieldGroup>
          <Controller
            name="email"
            control={form.control}
            render={({ field, fieldState }) => (
              <Field data-invalid={fieldState.invalid}>
                <div className="flex items-center justify-between">
                  <FieldLabel htmlFor="login-form-email">
                    {t('email.label')}
                  </FieldLabel>

                  {fieldState.invalid && (
                    <FieldError errors={[fieldState.error]} />
                  )}
                </div>

                <Input
                  {...field}
                  id="login-form-email"
                  aria-invalid={fieldState.invalid}
                  placeholder={t('email.placeholder')}
                  autoComplete="email"
                />
              </Field>
            )}
          />
          <Controller
            name="password"
            control={form.control}
            render={({ field, fieldState }) => (
              <Field data-invalid={fieldState.invalid}>
                <div className="flex items-center justify-between">
                  <FieldLabel htmlFor="login-form-password">
                    {t('password.label')}
                  </FieldLabel>

                  {fieldState.invalid && (
                    <FieldError errors={[fieldState.error]} />
                  )}
                </div>

                <Input
                  {...field}
                  id="login-form-password"
                  type="password"
                  aria-invalid={fieldState.invalid}
                  placeholder={t('password.placeholder')}
                  autoComplete="current-password"
                />
              </Field>
            )}
          />

          <div>
            <Button className="w-full" type="submit">
              {t('submit.label')}
            </Button>

            <Button className="w-full" variant="link">
              <Link href="/auth/register">{t('links.register.label')}</Link>
            </Button>
          </div>
        </FieldGroup>
      </form>
    </div>
  );
}
