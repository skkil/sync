import { zodResolver } from '@hookform/resolvers/zod';
import { useDebounce } from '@uidotdev/usehooks';
import { useTranslations } from 'next-intl';
import { forwardRef, useEffect, useImperativeHandle } from 'react';
import { Controller, useForm } from 'react-hook-form';
import z from 'zod';

import { FieldError } from '@/components/ui/field';
import {
  InputGroup,
  InputGroupAddon,
  InputGroupInput,
} from '@/components/ui/input-group';
import { useUpdateProfileMutation } from '@/features/profile/api/update-profile';
import { useGetHandleAvailabilityQuery } from '@/features/user/api/get-handle-availability';
import {
  MAXIMUM_HANDLE_LENGTH,
  MINIMUM_HANDLE_LENGTH,
} from '@/features/user/constants/handle';

import { OnboardingStepContentProps, OnboardingStepContentRef } from '../page';

export const ChooseHandle = forwardRef<
  OnboardingStepContentRef,
  OnboardingStepContentProps
>(({ onStateChange }, ref) => {
  const t = useTranslations('pages.onboarding.steps.choose-handle');

  const ChooseHandleFormSchema = z.object({
    handle: z
      .string()
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

  type ChooseHandleFormValues = z.infer<typeof ChooseHandleFormSchema>;
  const form = useForm<ChooseHandleFormValues>({
    resolver: zodResolver(ChooseHandleFormSchema),
    mode: 'onChange',
    defaultValues: {
      handle: '',
    },
  });

  const handle = form.watch('handle');
  const debouncedHandle = useDebounce(handle, 500);

  const {
    data: handleAvailability,
    isFetching: isGetHandleAvailabilityPending,
  } = useGetHandleAvailabilityQuery(debouncedHandle);

  const { mutate: updateProfile } = useUpdateProfileMutation();

  useEffect(() => {
    if (handleAvailability) {
      if (!handleAvailability.available) {
        form.setError('handle', {
          message: t('form.errors.handle_in_use'),
        });
      } else {
        form.clearErrors('handle');
      }
    }

    const isValid =
      form.formState.isValid && handleAvailability?.available === true;

    onStateChange({
      isPending: isGetHandleAvailabilityPending || handle !== debouncedHandle,
      isValid,
    });
  }, [
    t,
    isGetHandleAvailabilityPending,
    handleAvailability,
    form,
    handle,
    debouncedHandle,
    onStateChange,
  ]);

  useImperativeHandle(ref, () => ({
    submit: () => {
      updateProfile({
        handle,
      });
    },
  }));

  return (
    <Controller
      name="handle"
      control={form.control}
      render={({ field, fieldState }) => {
        return (
          <div>
            <InputGroup>
              <InputGroupAddon>@</InputGroupAddon>
              <InputGroupInput
                aria-invalid={fieldState.invalid}
                type="text"
                {...field}
              />
            </InputGroup>

            <div className="h-3 p-1">
              <FieldError errors={[fieldState.error]} />
            </div>
          </div>
        );
      }}
    />
  );
});
ChooseHandle.displayName = 'ChooseHandle';
