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
import RegisterForm from './_components/RegisterForm';

export default function Register() {
  const t = useTranslations('pages.register');

  return (
    <Card className="w-full max-w-md">
      <CardHeader>
        <CardTitle>{t('title')}</CardTitle>
        <CardDescription>{t('description')}</CardDescription>
      </CardHeader>

      <CardContent>
        <RegisterForm />
        <Separator className="my-3" />
        <OAuthProviders />
      </CardContent>
    </Card>
  );
}
