import { zodResolver } from '@hookform/resolvers/zod';
import { useTranslations } from 'next-intl';
import { useState } from 'react';
import { Controller, useForm } from 'react-hook-form';
import { toast } from 'sonner';
import z from 'zod';

import { Button } from '@/components/ui/button';
import { Checkbox } from '@/components/ui/checkbox';
import {
  Dialog,
  DialogContent,
  DialogDescription,
  DialogFooter,
  DialogHeader,
  DialogTitle,
  DialogTrigger,
} from '@/components/ui/dialog';
import { FieldError } from '@/components/ui/field';
import { Label } from '@/components/ui/label';
import { Rating } from '@/components/ui/rating';
import { Textarea } from '@/components/ui/textarea';
import { useCreateReviewMutation } from '@/features/review/api/create-review';
import { ProviderType } from '@/types/provider';

interface ProviderReviewDialogProps {
  providerType: ProviderType;
  providerId: string;
}

const ProviderReviewFormSchema = z.discriminatedUnion('type', [
  z.object({
    type: z.enum(ProviderType).extract(['SCHOOL']),
    review: z.string().trim().max(1000),
    isAnonymous: z.boolean(),
    academicQuality: z.number().min(0).max(5),
    campusFacilities: z.number().min(0).max(5),
    studentLife: z.number().min(0).max(5),
    valueForMoney: z.number().min(0).max(5),
  }),
  z.object({
    type: z.enum(ProviderType).extract(['LAB']),
    review: z.string().trim().max(1000),
    isAnonymous: z.boolean(),
    professorPersonality: z.number().min(0).max(5),
    labAtmosphere: z.number().min(0).max(5),
    workLifeBalance: z.number().min(0).max(5),
    compensation: z.number().min(0).max(5),
  }),
]);

type ProviderReviewFormValues = z.infer<typeof ProviderReviewFormSchema>;

type SchoolRatingFields =
  | 'academicQuality'
  | 'campusFacilities'
  | 'studentLife'
  | 'valueForMoney';
type LabRatingFields =
  | 'professorPersonality'
  | 'labAtmosphere'
  | 'workLifeBalance'
  | 'compensation';
type RatingFields = SchoolRatingFields | LabRatingFields;

export default function ProviderReviewDialog({
  providerType,
  providerId,
}: ProviderReviewDialogProps) {
  const t = useTranslations('pages.provider.review');

  const [open, setOpen] = useState(false);

  const form = useForm<ProviderReviewFormValues>({
    resolver: zodResolver(ProviderReviewFormSchema),
    defaultValues: getDefaultValues(providerType),
  });

  const { mutate: createReview } = useCreateReviewMutation(providerId);

  const formSubmitHandler = (values: ProviderReviewFormValues) => {
    createReview(values, {
      onSuccess: () => {
        form.reset();
        setOpen(false);
      },
      onError: () => {
        toast.error(t('form.submit.error'));
      },
    });
  };

  if (
    providerType !== ProviderType.SCHOOL &&
    providerType !== ProviderType.LAB
  ) {
    return null;
  }

  return (
    <Dialog open={open} onOpenChange={setOpen}>
      <DialogTrigger asChild>
        <Button>{t('button')}</Button>
      </DialogTrigger>
      <DialogContent>
        <form onSubmit={form.handleSubmit(formSubmitHandler)}>
          <DialogHeader>
            <DialogTitle>{t('form.title')}</DialogTitle>
            <DialogDescription>{t('form.description')}</DialogDescription>
          </DialogHeader>

          <Controller
            name="review"
            control={form.control}
            render={({ field, fieldState }) => (
              <div>
                <Textarea
                  value={field.value}
                  onChange={(e) => field.onChange(e.target.value)}
                  className="my-4 h-32"
                  aria-invalid={!!fieldState.error}
                />
                {fieldState.error && (
                  <FieldError className="-mt-2 mb-2">
                    {fieldState.error.message}
                  </FieldError>
                )}
              </div>
            )}
          />

          <div>
            {getRatingFields(providerType).map((fieldName) => (
              <Controller
                key={fieldName}
                name={fieldName as keyof ProviderReviewFormValues}
                control={form.control}
                render={({ field, fieldState }) => (
                  <div>
                    <div className="flex items-center justify-between w-full my-2">
                      <Label>{t(`form.fields.${fieldName}`)}</Label>
                      <Rating
                        value={Number(field.value)}
                        onValueChange={(value) => field.onChange(value)}
                      />
                    </div>
                    {fieldState.error && (
                      <FieldError className="text-xs -mt-1 mb-1">
                        {fieldState.error.message}
                      </FieldError>
                    )}
                  </div>
                )}
              />
            ))}
          </div>

          <DialogFooter>
            <div className="flex justify-between w-full">
              <Controller
                name="isAnonymous"
                control={form.control}
                render={({ field }) => (
                  <div className="flex items-center gap-2">
                    <Checkbox
                      id="anonymous-checkbox"
                      checked={field.value}
                      onCheckedChange={field.onChange}
                    />
                    <Label
                      htmlFor="anonymous-checkbox"
                      className="cursor-pointer"
                    >
                      {t('form.anonymous')}
                    </Label>
                  </div>
                )}
              />

              <Button type="submit">{t('form.submit.label')}</Button>
            </div>
          </DialogFooter>
        </form>
      </DialogContent>
    </Dialog>
  );
}

function getRatingFields(type: ProviderType): readonly RatingFields[] {
  switch (type) {
    case ProviderType.SCHOOL:
      return [
        'academicQuality',
        'campusFacilities',
        'studentLife',
        'valueForMoney',
      ] as const;
    case ProviderType.LAB:
      return [
        'professorPersonality',
        'labAtmosphere',
        'workLifeBalance',
        'compensation',
      ] as const;
    default:
      return [] as const;
  }
}

function getDefaultValues(type: ProviderType): ProviderReviewFormValues {
  const base = { review: '', isAnonymous: false };

  switch (type) {
    case ProviderType.SCHOOL:
      return {
        ...base,
        type: ProviderType.SCHOOL,
        academicQuality: 0,
        campusFacilities: 0,
        studentLife: 0,
        valueForMoney: 0,
      };
    case ProviderType.LAB:
    default:
      return {
        ...base,
        type: ProviderType.LAB,
        professorPersonality: 0,
        labAtmosphere: 0,
        workLifeBalance: 0,
        compensation: 0,
      };
  }
}
