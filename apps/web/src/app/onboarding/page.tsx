'use client';

import { useTranslations } from 'next-intl';
import { useRouter } from 'next/navigation';
import { forwardRef, useEffect, useRef, useState } from 'react';

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

import { ChooseHandle } from './_components/ChooseHandle';

export interface OnboardingStepContentRef {
  submit: () => void;
}

export interface OnboardingStepContentProps {
  onStateChange: (state: { isPending: boolean; isValid: boolean }) => void;
}

const steps: {
  id: string;
  content: ReturnType<
    typeof forwardRef<OnboardingStepContentRef, OnboardingStepContentProps>
  > | null;
}[] = [
  {
    id: 'welcome',
    content: null,
  },
  {
    id: 'choose-handle',
    content: ChooseHandle,
  },
  {
    id: 'finished',
    content: null,
  },
];

export default function Onboarding() {
  const t = useTranslations('pages.onboarding');

  const router = useRouter();
  const { data: session, isPending: isSessionPending } = useSession();

  const contentRef = useRef<OnboardingStepContentRef | null>(null);

  const [stepIndex, setStep] = useState(0);
  const [state, setState] = useState({
    isPending: false,
    isValid: true,
  });

  const { mutate: updateProfile } = useUpdateProfileMutation();

  useEffect(() => {
    if (!isSessionPending && !session) {
      router.push('/auth/login');
    }
  }, [session, isSessionPending, router]);

  if (isSessionPending) {
    return <Spinner />;
  }

  const previousButtonClickHandler = () => {
    setStep(stepIndex - 1);
  };

  const nextButtonClickHandler = () => {
    if (!state.isPending && state.isValid) {
      contentRef.current?.submit();
      setStep(stepIndex + 1);
      setState({
        isPending: false,
        isValid: true,
      });
    }
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

  const step = steps[stepIndex];

  return (
    <Card className="w-full max-w-md">
      <CardHeader>
        <CardTitle className="flex justify-between items-center">
          <span>{t(`steps.${step?.id}.title`)}</span>
          <span className="text-xs">
            {stepIndex + 1} / {steps.length}
          </span>
        </CardTitle>
        <CardDescription>{t(`steps.${step?.id}.description`)}</CardDescription>
      </CardHeader>

      <CardContent>
        {step?.content ? (
          <step.content
            ref={contentRef}
            onStateChange={({ isPending, isValid }) => {
              setState({ isPending, isValid });
            }}
          />
        ) : null}
      </CardContent>

      <CardFooter className="self-end flex gap-4">
        {stepIndex > 0 && (
          <Button variant="outline" onClick={previousButtonClickHandler}>
            {t('actions.previous')}
          </Button>
        )}

        {stepIndex === steps.length - 1 ? (
          <Button onClick={finishedButtonClickHandler}>
            {t('actions.finish')}
          </Button>
        ) : (
          <Button
            isPending={state.isPending}
            disabled={!state.isValid}
            onClick={nextButtonClickHandler}
          >
            {t('actions.next')}
          </Button>
        )}
      </CardFooter>
    </Card>
  );
}
