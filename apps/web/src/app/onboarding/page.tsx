'use client';

import { useTranslations } from 'next-intl';
import { useRouter } from 'next/navigation';
import { useEffect, useState } from 'react';

import { Button } from '@/components/ui/button';
import {
  Card,
  CardContent,
  CardDescription,
  CardFooter,
  CardHeader,
  CardTitle,
} from '@/components/ui/card';
import { Spinner } from '@/components/ui/spinner';
import { useUpdateProfileMutation } from '@/features/profile/api/update-profile';
import { useSession } from '@/lib/auth/client';

const steps = [
  {
    id: 'welcome',
  },
  {
    id: 'finished',
  },
];

export default function Onboarding() {
  const t = useTranslations('pages.onboarding');

  const router = useRouter();
  const { data: session, isPending } = useSession();

  const [step, setStep] = useState(0);
  const { mutate: updateProfile } = useUpdateProfileMutation();

  useEffect(() => {
    if (!isPending && !session) {
      router.push('/auth/login');
    }
  }, [session, isPending, router]);

  if (isPending) {
    return <Spinner />;
  }

  const previousButtonClickHandler = () => {
    setStep(step - 1);
  };

  const nextButtonClickHandler = () => {
    setStep(step + 1);
  };

  const finishedButtonClickHandler = () => {
    updateProfile(
      {
        isOnboarded: true,
      },
      {
        onSuccess: () => {
          router.push('/');
        },
      },
    );
  };

  return (
    <Card className="w-full max-w-md">
      <CardHeader>
        <CardTitle className="flex justify-between items-center">
          <span>{t(`steps.${steps[step]?.id}.title`)}</span>
          <span className="text-xs">
            {step + 1} / {steps.length}
          </span>
        </CardTitle>
        <CardDescription>
          {t(`steps.${steps[step]?.id}.description`)}
        </CardDescription>
      </CardHeader>

      <CardContent></CardContent>

      <CardFooter className="self-end flex gap-4">
        {step > 0 && (
          <Button variant="outline" onClick={previousButtonClickHandler}>
            {t('actions.previous')}
          </Button>
        )}

        {step === steps.length - 1 ? (
          <Button onClick={finishedButtonClickHandler}>
            {t('actions.finish')}
          </Button>
        ) : (
          <Button onClick={nextButtonClickHandler}>{t('actions.next')}</Button>
        )}
      </CardFooter>
    </Card>
  );
}
