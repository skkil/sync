'use client';

import { zodResolver } from '@hookform/resolvers/zod';
import { useTranslations } from 'next-intl';
import Link from 'next/link';
import { useRouter } from 'next/navigation';
import { Controller, useForm } from 'react-hook-form';
import { toast } from 'sonner';
import * as z from 'zod';

import { Button } from '@/components/ui/button';
import {
  Field,
  FieldError,
  FieldGroup,
  FieldLabel,
} from '@/components/ui/field';
import { Input } from '@/components/ui/input';
import { useRegisterMutation } from '@/features/auth/api/register';
import SyncError, { ErrorCode } from '@/lib/error';

const MIN_PASSWORD_LENGTH = 8;

export default function RegisterForm() {
  const t = useTranslations('pages.register.form');

  const { mutateAsync: register, isPending } = useRegisterMutation();
  const router = useRouter();

  const RegisterFormSchema = z
    .object({
      email: z
        .email({
          error: t('errors.invalid_email'),
        })
        .nonoptional({
          error: t('errors.required_email'),
        }),
      password: z
        .string()
        .nonempty({
          error: t('errors.required_password'),
        })
        .min(MIN_PASSWORD_LENGTH, {
          error: t('errors.password_min_length', {
            length: MIN_PASSWORD_LENGTH,
          }),
        }),
      confirmPassword: z.string().nonempty({
        error: t('errors.required_confirm_password'),
      }),
    })
    .superRefine((val, ctx) => {
      if (val.password !== val.confirmPassword) {
        ctx.addIssue({
          code: 'custom',
          message: t('errors.passwords_must_match'),
          path: ['confirmPassword'],
        });
      }
    });

  type RegisterFormValues = z.infer<typeof RegisterFormSchema>;

  const form = useForm<RegisterFormValues>({
    resolver: zodResolver(RegisterFormSchema),
    defaultValues: {
      email: '',
      password: '',
      confirmPassword: '',
    },
  });

  const onFormSubmit = async (values: RegisterFormValues) => {
    if (isPending) {
      return;
    }

    try {
      await register({
        email: values.email,
        password: values.password,
      });

      sessionStorage.setItem('pendingEmail', values.email);
      router.push('/auth/verify-email');
    } catch (error) {
      console.error('Registration error:', error);
      if (error instanceof SyncError) {
        const { code } = error;

        if (code === ErrorCode.USER_ALREADY_EXISTS) {
          toast.error(t('errors.user-already-exists'));
        }
      }
    }
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
                  <FieldLabel htmlFor="register-form-email">
                    {t('email.label')}
                  </FieldLabel>

                  {fieldState.invalid && (
                    <FieldError errors={[fieldState.error]} />
                  )}
                </div>

                <Input
                  {...field}
                  id="register-form-email"
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
                  <FieldLabel htmlFor="register-form-password">
                    {t('password.label')}
                  </FieldLabel>

                  {fieldState.invalid && (
                    <FieldError errors={[fieldState.error]} />
                  )}
                </div>

                <Input
                  {...field}
                  id="register-form-password"
                  type="password"
                  aria-invalid={fieldState.invalid}
                  placeholder={t('password.placeholder')}
                  autoComplete="new-password"
                />
              </Field>
            )}
          />
          <Controller
            name="confirmPassword"
            control={form.control}
            render={({ field, fieldState }) => (
              <Field data-invalid={fieldState.invalid}>
                <div className="flex items-center justify-between">
                  <FieldLabel htmlFor="register-form-confirm-password">
                    {t('confirm_password.label')}
                  </FieldLabel>

                  {fieldState.invalid && (
                    <FieldError errors={[fieldState.error]} />
                  )}
                </div>

                <Input
                  {...field}
                  id="register-form-confirm-password"
                  type="password"
                  aria-invalid={fieldState.invalid}
                  placeholder={t('confirm_password.placeholder')}
                  autoComplete="new-password"
                />
              </Field>
            )}
          />

          <div>
            <Button className="w-full" type="submit" isPending={isPending}>
              {t('submit.label')}
            </Button>

            <Button className="w-full" variant="link" type="button">
              <Link href="/auth/login">{t('links.login.label')}</Link>
            </Button>
          </div>
        </FieldGroup>
      </form>
    </div>
  );
}
