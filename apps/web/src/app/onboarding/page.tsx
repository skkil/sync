'use client';

import { useTranslations } from 'next-intl';
import { useRouter } from 'next/navigation';
import { forwardRef, useCallback, useRef, useState } from 'react';
import { toast } from 'sonner';

import { useUpdateProfile } from '@/components/feature/profile/hooks/useUpdateProfile';
import { Button } from '@/components/ui/button';
import { useSession } from '@/lib/auth/client';
import ROUTES from '@/util/routes';

import { ChooseHandle } from './_components/ChooseHandle';
import { RecommendedFollows } from './_components/RecommendedFollows';

export interface OnboardingStepContentRef {
  submit: (onSuccess: () => void) => void;
}

export interface OnboardingStepContentProps {
  onStateChange: (state: { isPending: boolean; isValid: boolean }) => void;
}

const steps: {
  id: 'welcome' | 'choose-handle' | 'follow' | 'finished';
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
    id: 'follow',
    content: RecommendedFollows,
  },
  {
    id: 'finished',
    content: null,
  },
];

export default function Onboarding() {
  const t = useTranslations('pages.onboarding');

  const router = useRouter();
  const { refetch: refetchSession } = useSession();

  const contentRef = useRef<OnboardingStepContentRef | null>(null);

  const [stepIndex, setStep] = useState(0);
  const [state, setState] = useState({
    isPending: false,
    isValid: true,
  });

  const { mutate: updateProfile, isPending: isFinishing } = useUpdateProfile({
    onSuccess: async () => {
      await refetchSession();
      router.replace(ROUTES.HOME());
    },
  });

  const handleStateChange = useCallback(
    ({ isPending, isValid }: { isPending: boolean; isValid: boolean }) => {
      setState({ isPending, isValid });
    },
    [],
  );

  const previousButtonClickHandler = () => {
    setStep(stepIndex - 1);
    setState({
      isPending: false,
      isValid: true,
    });
  };

  const nextButtonClickHandler = () => {
    if (!state.isPending && state.isValid) {
      setState({
        isPending: true,
        isValid: true,
      });

      if (contentRef.current) {
        contentRef.current.submit(() => {
          setStep(stepIndex + 1);
          setState({
            isPending: false,
            isValid: true,
          });
        });
      } else {
        setStep(stepIndex + 1);
        setState({
          isPending: false,
          isValid: true,
        });
      }
    }
  };

  const finishedButtonClickHandler = () => {
    updateProfile(
      {
        data: {
          isOnboarded: true,
        },
      },
      {
        onError: () => {
          toast.error(t('errors.finish'));
        },
      },
    );
  };

  const step = steps[stepIndex];

  return (
    <div className="flex flex-col gap-8">
      <div className="flex gap-1.5">
        {steps.map((s, index) => (
          <div
            key={s.id}
            className={`h-1 flex-1 rounded-full transition-colors ${
              index <= stepIndex ? 'bg-primary' : 'bg-muted'
            }`}
          />
        ))}
      </div>

      <div className="flex flex-col gap-8">
        <div>
          <h1 className="text-2xl font-light mb-2">
            {t(`steps.${step?.id ?? 'welcome'}.title`)}
          </h1>
          <p className="text-muted-foreground">
            {t(`steps.${step?.id ?? 'welcome'}.description`)}
          </p>
        </div>

        {step?.content ? (
          <step.content ref={contentRef} onStateChange={handleStateChange} />
        ) : null}

        <div className="flex justify-end gap-4">
          {stepIndex > 0 && (
            <Button variant="outline" onClick={previousButtonClickHandler}>
              {t('actions.previous')}
            </Button>
          )}

          {stepIndex === steps.length - 1 ? (
            <Button
              isPending={isFinishing}
              onClick={finishedButtonClickHandler}
            >
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
        </div>
      </div>
    </div>
  );
}
