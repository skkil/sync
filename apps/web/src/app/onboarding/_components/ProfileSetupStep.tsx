'use client';

import { zodResolver } from '@hookform/resolvers/zod';
import { useDebounce } from '@uidotdev/usehooks';
import { useTranslations } from 'next-intl';
import { forwardRef, useEffect, useImperativeHandle, useRef } from 'react';
import { Controller, useForm } from 'react-hook-form';
import z from 'zod';

import { useGetHandleAvailability } from '@/api/__generated__/user/user';
import { useUpdateProfile } from '@/components/feature/profile/hooks/useUpdateProfile';
import {
  Field,
  FieldError,
  FieldGroup,
  FieldLabel,
} from '@/components/ui/field';
import { Input } from '@/components/ui/input';
import {
  InputGroup,
  InputGroupAddon,
  InputGroupInput,
} from '@/components/ui/input-group';
import { useSession } from '@/lib/auth/client';

import { OnboardingStepContentProps, OnboardingStepContentRef } from '../page';

const MAXIMUM_HANDLE_LENGTH = 255;
const MINIMUM_HANDLE_LENGTH = 6;

export const ProfileSetupStep = forwardRef<
  OnboardingStepContentRef,
  OnboardingStepContentProps
>(({ onStateChange }, ref) => {
  const t = useTranslations('pages.onboarding.steps.profile');

  const ProfileSetupFormSchema = z.object({
    name: z.string().min(1, { error: t('form.errors.required_name') }),
    handle: z
      .string()
      .regex(/^[a-zA-Z0-9_]+$/, {
        error: t('form.errors.invalid_characters'),
      })
      .min(MINIMUM_HANDLE_LENGTH, {
        error: t('form.errors.minimum_length', {
          length: MINIMUM_HANDLE_LENGTH,
        }),
      })
      .max(MAXIMUM_HANDLE_LENGTH, {
        error: t('form.errors.maximum_length', {
          length: MAXIMUM_HANDLE_LENGTH,
        }),
      }),
  });

  type ProfileSetupFormValues = z.infer<typeof ProfileSetupFormSchema>;
  const form = useForm<ProfileSetupFormValues>({
    resolver: zodResolver(ProfileSetupFormSchema),
    mode: 'onChange',
    defaultValues: {
      name: '',
      handle: '',
    },
  });

  const { data: session, refetch: refetchSession } = useSession();

  const hasPrefilled = useRef(false);
  useEffect(() => {
    if (!hasPrefilled.current && session?.user) {
      hasPrefilled.current = true;
      form.reset({
        name: session.user.name ?? '',
        handle: session.user.handle ?? '',
      });
    }
  }, [session, form]);

  const name = form.watch('name');
  const handle = form.watch('handle');

  const debouncedHandle = useDebounce(handle, 500);
  const isHandleUnchanged = debouncedHandle === (session?.user.handle ?? '');

  const {
    data: handleAvailabilityData,
    isPending: isGetHandleAvailabilityPending,
  } = useGetHandleAvailability(
    { handle: debouncedHandle },
    {
      query: {
        enabled:
          !isHandleUnchanged &&
          debouncedHandle.length >= MINIMUM_HANDLE_LENGTH &&
          debouncedHandle.length <= MAXIMUM_HANDLE_LENGTH,
      },
    },
  );

  const { mutate: updateProfile } = useUpdateProfile();

  useEffect(() => {
    if (!isHandleUnchanged && handleAvailabilityData) {
      const { available } = handleAvailabilityData.data;

      if (!available) {
        form.setError('handle', {
          message: t('form.errors.handle_in_use'),
        });
      } else {
        form.clearErrors('handle');
      }
    } else if (isHandleUnchanged) {
      form.clearErrors('handle');
    }

    const isHandleValid = isHandleUnchanged
      ? true
      : handleAvailabilityData?.data.available === true;

    onStateChange({
      isPending:
        !isHandleUnchanged &&
        (isGetHandleAvailabilityPending || handle !== debouncedHandle),
      isValid: form.formState.isValid && isHandleValid,
    });
  }, [
    t,
    form,
    handle,
    debouncedHandle,
    isHandleUnchanged,
    isGetHandleAvailabilityPending,
    handleAvailabilityData,
    onStateChange,
  ]);

  useImperativeHandle(ref, () => ({
    submit: (onSuccess) => {
      const changes: { name?: string; handle?: string } = {};

      if (name !== (session?.user.name ?? '')) {
        changes.name = name;
      }
      if (handle !== (session?.user.handle ?? '')) {
        changes.handle = handle;
      }

      if (Object.keys(changes).length === 0) {
        onSuccess();
        return;
      }

      updateProfile(
        { data: changes },
        {
          onSuccess: async () => {
            await refetchSession();
            onSuccess();
          },
        },
      );
    },
  }));

  return (
    <FieldGroup>
      <Controller
        name="name"
        control={form.control}
        render={({ field, fieldState }) => (
          <Field data-invalid={fieldState.invalid}>
            <FieldLabel>{t('form.name.label')}</FieldLabel>
            <Input
              {...field}
              aria-invalid={fieldState.invalid}
              placeholder={t('form.name.placeholder')}
            />
            <div className="h-3 p-1">
              <FieldError errors={[fieldState.error]} />
            </div>
          </Field>
        )}
      />

      <Controller
        name="handle"
        control={form.control}
        render={({ field, fieldState }) => (
          <Field data-invalid={fieldState.invalid}>
            <FieldLabel>{t('form.handle.label')}</FieldLabel>
            <InputGroup>
              <InputGroupAddon>@</InputGroupAddon>
              <InputGroupInput
                aria-invalid={fieldState.invalid}
                type="text"
                placeholder={t('form.handle.placeholder')}
                {...field}
              />
            </InputGroup>
            <div className="h-3 p-1">
              <FieldError errors={[fieldState.error]} />
            </div>
          </Field>
        )}
      />
    </FieldGroup>
  );
});
ProfileSetupStep.displayName = 'ProfileSetupStep';
