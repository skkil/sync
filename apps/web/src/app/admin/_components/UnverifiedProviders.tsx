import { useTranslations } from 'next-intl';
import { useState } from 'react';
import { toast } from 'sonner';

import { Button } from '@/components/ui/button';
import {
  Card,
  CardContent,
  CardDescription,
  CardFooter,
  CardHeader,
  CardTitle,
} from '@/components/ui/card';
import {
  Pagination,
  PaginationContent,
  PaginationItem,
  PaginationLink,
  PaginationNext,
  PaginationPrevious,
} from '@/components/ui/pagination';
import { ScrollArea } from '@/components/ui/scroll-area';
import { Spinner } from '@/components/ui/spinner';
import { useGetUnverifiedProvidersQuery } from '@/features/provider/api/get-unverified-providers';
import { useVerifyProviderMutation } from '@/features/provider/api/verify-provider';

const PAGE_SIZE = 50;

export default function UnverifiedProviders() {
  const t = useTranslations('pages.admin.unverified-providers');

  const [currentPage, setCurrentPage] = useState(0);
  const [verifyingProviderId, setVerifyingProviderId] = useState<string | null>(
    null,
  );

  const { data: unverifiedProviders } = useGetUnverifiedProvidersQuery(
    currentPage,
    PAGE_SIZE,
  );

  const { mutate: verifyProvider, isPending } = useVerifyProviderMutation();

  const hasPreviousPage = currentPage > 0;
  const hasNextPage =
    unverifiedProviders &&
    currentPage < unverifiedProviders.page.totalPages - 1;

  const verifyProviderHandler = (providerId: string) => {
    setVerifyingProviderId(providerId);

    verifyProvider(providerId, {
      onSuccess: () => {
        toast.success(t('messages.success'));
      },
      onError: () => {
        toast.error(t('messages.error'));
      },
      onSettled: () => {
        setVerifyingProviderId(null);
      },
    });
  };

  return (
    <Card>
      <CardHeader>
        <CardTitle>{t('title')}</CardTitle>
        <CardDescription>{t('description')}</CardDescription>
      </CardHeader>

      <CardContent>
        <ScrollArea className="h-96">
          {unverifiedProviders ? (
            unverifiedProviders.content.map((provider) => (
              <div
                key={provider.id}
                className="flex items-center justify-between py-1"
              >
                {provider.name}

                <div>
                  <Button
                    isPending={
                      isPending && verifyingProviderId === provider.id
                    }
                    disabled={
                      isPending && verifyingProviderId !== provider.id
                    }
                    onClick={() => {
                      verifyProviderHandler(provider.id);
                    }}
                  >
                    {t('actions.verify')}
                  </Button>
                </div>
              </div>
            ))
          ) : (
            <Spinner />
          )}
        </ScrollArea>
      </CardContent>

      <CardFooter>
        <Pagination>
          <PaginationContent>
            <PaginationItem>
              <PaginationPrevious
                className={
                  hasPreviousPage ? '' : 'pointer-events-none opacity-50'
                }
                onClick={() => {
                  if (hasPreviousPage) {
                    setCurrentPage(currentPage - 1);
                  }
                }}
              />
            </PaginationItem>

            {Array.from({
              length: unverifiedProviders?.page.totalPages || 0,
            }).map((_, index) => (
              <PaginationItem key={index}>
                <PaginationLink
                  isActive={currentPage === index}
                  onClick={() => setCurrentPage(index)}
                  aria-current={currentPage === index ? 'page' : undefined}
                >
                  {index + 1}
                </PaginationLink>
              </PaginationItem>
            ))}

            <PaginationItem>
              <PaginationNext
                className={hasNextPage ? '' : 'pointer-events-none opacity-50'}
                onClick={() => {
                  if (hasNextPage) {
                    setCurrentPage(currentPage + 1);
                  }
                }}
              />
            </PaginationItem>
          </PaginationContent>
        </Pagination>
      </CardFooter>
    </Card>
  );
}
