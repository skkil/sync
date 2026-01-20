import { useTranslations } from 'next-intl';

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

export default function Login() {
  const t = useTranslations('pages.login');

  return (
    <Card className="w-full max-w-md">
      <CardHeader>
        <CardTitle>{t('title')}</CardTitle>
        <CardDescription>{t('description')}</CardDescription>
      </CardHeader>

      <CardContent>
        <LoginForm />
        <Separator className="my-3" />
        <OAuthProviders />
      </CardContent>
    </Card>
  );
}
