import { useTranslations } from 'next-intl';

import { useGetUnverifiedProvidersInfinite } from '@/api/__generated__/providers/providers';
import { Button } from '@/components/ui/button';
import {
  Card,
  CardContent,
  CardDescription,
  CardFooter,
  CardHeader,
  CardTitle,
} from '@/components/ui/card';
import { ScrollArea } from '@/components/ui/scroll-area';
import { Spinner } from '@/components/ui/spinner';
import { useVerifyProviderMutation } from '@/features/provider/api/verify-provider';

const PAGE_SIZE = 50;

export default function UnverifiedProviders() {
  const t = useTranslations('pages.admin.unverified-providers');

  const { data, fetchNextPage, hasNextPage, isFetchingNextPage, isPending } =
    useGetUnverifiedProvidersInfinite(
      { size: PAGE_SIZE.toString() },
      {
        query: {
          getNextPageParam: (lastPage) => {
            const providers = lastPage.data.providers;
            return providers?.hasNext ? providers.nextCursor : undefined;
          },
        },
      },
    );

  const { mutate: verifyProvider } = useVerifyProviderMutation();

  const providers =
    data?.pages.flatMap((page) => page.data.providers?.content ?? []) ?? [];

  return (
    <Card>
      <CardHeader>
        <CardTitle>{t('title')}</CardTitle>
        <CardDescription>{t('description')}</CardDescription>
      </CardHeader>

      <CardContent>
        <ScrollArea className="h-96">
          {providers.length > 0 ? (
            providers.map((provider) => (
              <div
                key={provider.id}
                className="flex items-center justify-between py-1"
              >
                {provider.name}

                <div>
                  <Button
                    onClick={() => {
                      verifyProvider(provider.id);
                    }}
                  >
                    {t('actions.verify')}
                  </Button>
                </div>
              </div>
            ))
          ) : isPending ? (
            <div className="flex items-center justify-center py-8">
              <Spinner />
            </div>
          ) : (
            <div className="py-8 text-center text-muted-foreground">
              {t('no-providers')}
            </div>
          )}
        </ScrollArea>
      </CardContent>

      <CardFooter className="justify-center">
        {hasNextPage && (
          <Button
            onClick={() => fetchNextPage()}
            disabled={isFetchingNextPage}
            variant="outline"
          >
            {isFetchingNextPage ? t('loading') : t('load-more')}
          </Button>
        )}
      </CardFooter>
    </Card>
  );
}
