import { getTranslations } from 'next-intl/server';

import {
  Card,
  CardContent,
  CardDescription,
  CardHeader,
  CardTitle,
} from '@/components/ui/card';
import { Separator } from '@/components/ui/separator';

import OAuthProviders from '../_components/OAuthProviders';
import LoginForm from './_components/LoginForm';

interface LoginPageProps {
  searchParams: Promise<{
    verified?: string;
  }>;
}

export default async function Login({ searchParams }: LoginPageProps) {
  const t = await getTranslations('pages.login');
  const { verified } = await searchParams;
  const isVerified = verified === 'true';

  return (
    <Card className="w-full max-w-md">
      <CardHeader>
        <CardTitle>{t('title')}</CardTitle>
        <CardDescription>{t('description')}</CardDescription>
      </CardHeader>

      <CardContent>
        <LoginForm isVerified={isVerified} />
        <Separator className="my-3" />
        <OAuthProviders />
      </CardContent>
    </Card>
  );
}
