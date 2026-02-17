import { zodResolver } from '@hookform/resolvers/zod';
import { useTranslations } from 'next-intl';
import { useState } from 'react';
import { Controller, useForm } from 'react-hook-form';
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
    review: z.string(),
    academicQuality: z.number().min(0).max(5),
    campusFacilities: z.number().min(0).max(5),
    studentLife: z.number().min(0).max(5),
    valueForMoney: z.number().min(0).max(5),
  }),
]);

export default function ProviderReviewDialog({
  providerType,
  providerId,
}: ProviderReviewDialogProps) {
  const t = useTranslations('pages.provider.review');

  const [open, setOpen] = useState(false);

  type ProviderReviewFormValues = z.infer<typeof ProviderReviewFormSchema>;

  const form = useForm<ProviderReviewFormValues>({
    resolver: zodResolver(ProviderReviewFormSchema),
    defaultValues: {
      type: ProviderType.SCHOOL,
      review: '',
      academicQuality: 0,
      campusFacilities: 0,
      studentLife: 0,
      valueForMoney: 0,
    },
  });

  const { mutate: createReview } = useCreateReviewMutation(providerId);

  const formSubmitHandler = (values: ProviderReviewFormValues) => {
    createReview(values, {
      onSuccess: () => {
        setOpen(false);
      },
    });
  };

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
            render={({ field }) => (
              <Textarea
                value={field.value}
                onChange={(e) => field.onChange(e.target.value)}
                className="my-4 h-32"
              />
            )}
          />

          <div>
            {getRatingFields(providerType).map((fieldName) => (
              <Controller
                key={fieldName}
                name={fieldName as keyof ProviderReviewFormValues}
                control={form.control}
                render={({ field }) => (
                  <div className="flex items-center justify-between w-full my-2">
                    <Label>{t(`form.fields.${fieldName}`)}</Label>
                    <Rating
                      value={field.value as number}
                      onValueChange={(value) => field.onChange(value)}
                    />
                  </div>
                )}
              />
            ))}
          </div>

          <DialogFooter>
            <div className="flex justify-between w-full">
              <div className="flex items-center gap-2">
                <Checkbox />
                <span>{t('form.anonymous')}</span>
              </div>

              <Button>{t('form.submit.label')}</Button>
            </div>
          </DialogFooter>
        </form>
      </DialogContent>
    </Dialog>
  );
}

function getRatingFields(type: ProviderType) {
  switch (type) {
    case ProviderType.SCHOOL:
      return [
        'academicQuality',
        'campusFacilities',
        'studentLife',
        'valueForMoney',
      ];
    default:
      return [];
  }
}
