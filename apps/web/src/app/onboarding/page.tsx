'use client';

import { useTranslations } from 'next-intl';
import { useRouter } from 'next/navigation';
import { forwardRef, useCallback, useMemo, useRef, useState } from 'react';
import { toast } from 'sonner';

import { useGetAuthenticatedUser } from '@/api/__generated__/profile/profile';
import { useOnboardProfile } from '@/components/feature/profile/hooks/useOnboardProfile';
import { Button } from '@/components/ui/button';
import ROUTES from '@/util/routes';

import { EmailVerificationStep } from './_components/EmailVerificationStep';
import { ProfileSetupStep } from './_components/ProfileSetupStep';
import { RecommendedFollows } from './_components/RecommendedFollows';
import { TagFollowStep } from './_components/TagFollowStep';

export interface OnboardingStepContentRef {
  submit: (onSuccess: () => void) => void;
}

export interface OnboardingStepContentProps {
  onStateChange: (state: { isPending: boolean; isValid: boolean }) => void;
}

type StepDefinition = {
  id: 'profile' | 'tags' | 'follow' | 'email-verification' | 'finished';
  content: ReturnType<
    typeof forwardRef<OnboardingStepContentRef, OnboardingStepContentProps>
  > | null;
};

const allSteps: StepDefinition[] = [
  {
    id: 'profile',
    content: ProfileSetupStep,
  },
  {
    id: 'tags',
    content: TagFollowStep,
  },
  {
    id: 'follow',
    content: RecommendedFollows,
  },
  {
    id: 'email-verification',
    content: EmailVerificationStep,
  },
  {
    id: 'finished',
    content: null,
  },
];

export default function Onboarding() {
  const t = useTranslations('pages.onboarding');

  const router = useRouter();
  const { data: profile, isPending: isProfilePending } =
    useGetAuthenticatedUser();

  const steps = useMemo(
    () =>
      allSteps.filter(
        (s) => s.id !== 'email-verification' || !profile?.data.isEmailVerified,
      ),
    [profile],
  );

  const contentRefs = useRef<Map<number, OnboardingStepContentRef | null>>(
    new Map(),
  );

  const [stepIndex, setStep] = useState(0);
  const [visitedSteps, setVisitedSteps] = useState<Set<number>>(
    () => new Set([0]),
  );
  const [state, setState] = useState({
    isPending: false,
    isValid: true,
  });

  const { mutate: onboardProfile, isPending: isFinishing } = useOnboardProfile({
    onSuccess: async () => {
      router.replace(ROUTES.HOME());
    },
    onError: () => {
      toast.error(t('errors.finish'));
    },
  });

  const handleStateChange = useCallback(
    ({ isPending, isValid }: { isPending: boolean; isValid: boolean }) => {
      setState({ isPending, isValid });
    },
    [],
  );

  const previousButtonClickHandler = () => {
    const targetStep = stepIndex - 1;
    setVisitedSteps((prev) => new Set(prev).add(targetStep));
    setStep(targetStep);
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

      const advance = () => {
        const targetStep = stepIndex + 1;
        setVisitedSteps((prev) => new Set(prev).add(targetStep));
        setStep(targetStep);
        setState({
          isPending: false,
          isValid: true,
        });
      };

      const currentRef = contentRefs.current.get(stepIndex);
      if (currentRef) {
        currentRef.submit(advance);
      } else {
        advance();
      }
    }
  };

  const finishedButtonClickHandler = () => {
    onboardProfile();
  };

  const step = steps[stepIndex];

  if (isProfilePending) {
    return null;
  }

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
            {t(`steps.${step?.id ?? 'profile'}.title`)}
          </h1>
          <p className="text-muted-foreground">
            {t(`steps.${step?.id ?? 'profile'}.description`)}
          </p>
        </div>

        {steps.map((s, index) => {
          if (!s.content || !visitedSteps.has(index)) {
            return null;
          }

          const StepContent = s.content;

          return (
            <div key={s.id} className={index === stepIndex ? '' : 'hidden'}>
              <StepContent
                ref={(el) => {
                  if (el) {
                    contentRefs.current.set(index, el);
                  } else {
                    contentRefs.current.delete(index);
                  }
                }}
                onStateChange={
                  index === stepIndex ? handleStateChange : () => {}
                }
              />
            </div>
          );
        })}

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
