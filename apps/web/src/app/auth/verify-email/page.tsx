'use client';

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
import { Input } from '@/components/ui/input';
import { server } from '@/lib/server';

export default function VerifyEmailPage() {
  const router = useRouter();
  const [email, setEmail] = useState('');
  const [code, setCode] = useState('');
  const [isLoading, setIsLoading] = useState(false);
  const [errorMessage, setErrorMessage] = useState('');

  useEffect(() => {
    const pendingEmail = sessionStorage.getItem('pendingEmail');

    if (!pendingEmail) {
      setErrorMessage(
        '인증할 이메일 정보가 없습니다. 다시 회원가입을 진행해주세요.',
      );
      return;
    }

    setEmail(pendingEmail);
  }, []);

  const handleCodeChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    const value = e.target.value.replace(/\D/g, '').slice(0, 6);
    setCode(value);
    setErrorMessage('');
  };

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();

    if (!email) {
      setErrorMessage(
        '인증할 이메일 정보가 없습니다. 다시 회원가입을 진행해주세요.',
      );
      return;
    }

    if (code.length !== 6) {
      setErrorMessage('6자리 코드를 입력해주세요.');
      return;
    }

    setIsLoading(true);
    try {
      await server.post('auth/verify-email', {
        json: { email, code },
      });

      sessionStorage.removeItem('pendingEmail');
      router.push('/');
    } catch (err: unknown) {
      const message =
        err instanceof Error
          ? err.message
          : '인증에 실패했습니다. 코드를 다시 확인해주세요.';
      setErrorMessage(message);
      setCode('');
    } finally {
      setIsLoading(false);
    }
  };

  return (
    <Card className="w-full max-w-md">
      <CardHeader>
        <CardTitle>이메일 인증</CardTitle>
        <CardDescription>
          {email
            ? `${email}로 전송된 6자리 코드를 입력해주세요.`
            : '이메일로 받으신 6자리 코드를 입력해주세요.'}
        </CardDescription>
      </CardHeader>

      <CardContent>
        <form onSubmit={handleSubmit} className="space-y-4">
          <div className="space-y-2">
            <label htmlFor="code" className="text-sm font-medium">
              인증 코드
            </label>
            <Input
              id="code"
              type="text"
              inputMode="numeric"
              placeholder="000000"
              value={code}
              onChange={handleCodeChange}
              maxLength={6}
              className="text-center text-2xl tracking-widest"
              autoComplete="off"
              disabled={isLoading}
            />
          </div>

          {errorMessage && (
            <div className="rounded-md border border-destructive/30 bg-destructive/10 p-3">
              <p className="text-sm text-destructive">{errorMessage}</p>
            </div>
          )}

          <Button
            type="submit"
            className="w-full"
            disabled={isLoading || code.length !== 6 || !email}
          >
            {isLoading ? '처리 중...' : '인증하기'}
          </Button>

          <Button
            type="button"
            variant="link"
            className="w-full"
            onClick={() => router.push('/auth/register')}
          >
            다시 회원가입
          </Button>
        </form>
      </CardContent>
    </Card>
  );
}
