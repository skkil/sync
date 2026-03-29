'use client';

import { useTranslations } from 'next-intl';
import { useRouter } from 'next/navigation';
import { useEffect, useState } from 'react';

import { Button } from '@/components/ui/button';
import {
  Card,
  CardContent,
  CardDescription,
  CardHeader,
  CardTitle,
} from '@/components/ui/card';
import {
  InputOTP,
  InputOTPGroup,
  InputOTPSlot,
} from '@/components/ui/input-otp';
import { server } from '@/lib/server';

export default function VerifyEmailPage() {
  const t = useTranslations('pages.verify-email');
  const router = useRouter();
  const [email, setEmail] = useState('');
  const [code, setCode] = useState('');
  const [isLoading, setIsLoading] = useState(false);
  const [isResending, setIsResending] = useState(false);
  const [isFetchingEmail, setIsFetchingEmail] = useState(true);
  const [errorMessage, setErrorMessage] = useState('');

  useEffect(() => {
    const fetchPendingEmail = async () => {
      setIsFetchingEmail(true);
      setErrorMessage('');

      try {
        const response = await server
          .get('auth/pending-email')
          .json<{ email: string }>();
        setEmail(response.email);
      } catch (err: unknown) {
        const message =
          err instanceof Error ? err.message : t('errors.not-found-email');
        setErrorMessage(message);
      } finally {
        setIsFetchingEmail(false);
      }
    };

    void fetchPendingEmail();
  }, []);

  const handleCodeChange = (value: string) => {
    const numericValue = value.replace(/\D/g, '').slice(0, 6);
    setCode(numericValue);
    setErrorMessage('');
  };

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();

    if (!email) {
      setErrorMessage(t('errors.not-found-email'));
      return;
    }

    if (code.length !== 6) {
      setErrorMessage(t('errors.invalid-code-len'));
      return;
    }

    setIsLoading(true);
    try {
      await server.post('auth/verify-email', {
        json: { code },
      });

      router.replace('/auth/login?verified=true');
    } catch (err: unknown) {
      const message =
        err instanceof Error ? err.message : t('errors.failed-verification');
      setErrorMessage(message);
      setCode('');
    } finally {
      setIsLoading(false);
    }
  };

  const handleResendCode = async () => {
    if (isResending) {
      return;
    }

    setIsResending(true);
    setErrorMessage('');

    try {
      await server.post('auth/resend-verification-code');

      setCode('');
    } catch (err: unknown) {
      const message =
        err instanceof Error ? err.message : t('errors.failed-resend');

      setErrorMessage(message);
    } finally {
      setIsResending(false);
    }
  };

  return (
    <Card className="w-full max-w-md">
      <CardHeader>
        <CardTitle>{t('email-verified.title')}</CardTitle>
        <CardDescription>{t('email-verified.sentToEmail')}</CardDescription>
      </CardHeader>

      <CardContent>
        <form onSubmit={handleSubmit} className="space-y-4">
          <div className="space-y-2">
            <label htmlFor="code" className="text-sm font-medium"></label>
            <InputOTP
              id="code"
              maxLength={6}
              value={code}
              onChange={handleCodeChange}
              disabled={isLoading || isResending || isFetchingEmail}
              containerClassName="justify-center"
            >
              <InputOTPGroup>
                <InputOTPSlot index={0} />
                <InputOTPSlot index={1} />
                <InputOTPSlot index={2} />
                <InputOTPSlot index={3} />
                <InputOTPSlot index={4} />
                <InputOTPSlot index={5} />
              </InputOTPGroup>
            </InputOTP>
          </div>

          {errorMessage && (
            <div className="rounded-md border border-destructive/30 bg-destructive/10 p-3">
              <p className="text-sm text-destructive">{errorMessage}</p>
            </div>
          )}

          <Button
            type="submit"
            className="w-full"
            disabled={
              isLoading ||
              isResending ||
              isFetchingEmail ||
              code.length !== 6 ||
              !email
            }
          >
            {isLoading
              ? t('email-verified.pending')
              : t('email-verified.submit')}
          </Button>

          <Button
            type="button"
            variant="outline"
            className="w-full"
            onClick={handleResendCode}
            disabled={isLoading || isResending || isFetchingEmail || !email}
          >
            {isResending
              ? t('email-verified.resending')
              : t('email-verified.resend')}
          </Button>

          <Button
            type="button"
            variant="link"
            className="w-full"
            onClick={() => router.push('/auth/register')}
          >
            {t('email-verified.re-register')}
          </Button>
        </form>
      </CardContent>
    </Card>
  );
}
