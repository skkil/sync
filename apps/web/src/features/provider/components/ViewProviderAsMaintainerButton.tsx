'use client';

import { useTranslations } from 'next-intl';
import Link from 'next/link';
import { useRouter, useSearchParams } from 'next/navigation';
import { useEffect } from 'react';

import { Button } from '@/components/ui/button';
import {
  Card,
  CardDescription,
  CardHeader,
  CardTitle,
} from '@/components/ui/card';

import useGetProviderQuery from '../api/get-provider';

const VIEW_AS_MEMBER_QUERY_KEY = 'm';

interface ViewProviderAsMaintainerButtonProps {
  id: string;
  href: string;
}

export default function ViewProviderAsMaintainerButton({
  id,
  href,
}: ViewProviderAsMaintainerButtonProps) {
  const t = useTranslations('components.provider.maintainer.viewing-as-public');

  const router = useRouter();
  const searchParams = useSearchParams();

  const viewAsMember =
    searchParams.get(VIEW_AS_MEMBER_QUERY_KEY) === 'true' || false;

  const { data: provider } = useGetProviderQuery(id);

  useEffect(() => {
    if (!viewAsMember && provider?.isMaintainer) {
      router.push(href);
    }
  }, [viewAsMember, router, href, provider]);

  if (!provider) {
    return null;
  }

  if (!provider.isMaintainer || !viewAsMember) {
    return null;
  }

  return (
    <Card>
      <CardHeader>
        <div className="flex items-center justify-between">
          <div>
            <CardTitle>
              <span>
                {t('title', {
                  name: provider.name,
                })}
              </span>
            </CardTitle>

            <CardDescription>{t('description')}</CardDescription>
          </div>

          <Button asChild>
            <Link href={href}>{t('view-as-admin')}</Link>
          </Button>
        </div>
      </CardHeader>
    </Card>
  );
}
