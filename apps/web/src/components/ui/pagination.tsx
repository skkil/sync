import {
  CaretLeftIcon,
  CaretRightIcon,
  DotsThreeIcon,
} from '@phosphor-icons/react';
import { useTranslations } from 'next-intl';
import * as React from 'react';

import { Button } from '@/components/ui/button';
import { cn } from '@/lib/utils';

function Pagination({ className, ...props }: React.ComponentProps<'nav'>) {
  const t = useTranslations('components.ui.pagination');

  return (
    <nav
      role="navigation"
      aria-label={t('navigation')}
      data-slot="pagination"
      className={cn('mx-auto flex w-full justify-center', className)}
      {...props}
    />
  );
}

function PaginationContent({
  className,
  ...props
}: React.ComponentProps<'ul'>) {
  return (
    <ul
      data-slot="pagination-content"
      className={cn('gap-1 flex items-center', className)}
      {...props}
    />
  );
}

function PaginationItem({ ...props }: React.ComponentProps<'li'>) {
  return <li data-slot="pagination-item" {...props} />;
}

type PaginationLinkProps = {
  isActive?: boolean;
} & Pick<React.ComponentProps<typeof Button>, 'size'> &
  React.ComponentProps<'a'>;

function PaginationLink({
  className,
  isActive,
  size = 'icon',
  ...props
}: PaginationLinkProps) {
  return (
    <Button
      asChild
      variant={isActive ? 'outline' : 'ghost'}
      size={size}
      className={cn(className)}
    >
      <a
        aria-current={isActive ? 'page' : undefined}
        data-slot="pagination-link"
        data-active={isActive}
        {...props}
      />
    </Button>
  );
}

function PaginationPrevious({
  className,
  text = '',
  disabled = false,
  ...props
}: React.ComponentProps<typeof PaginationLink> & {
  text?: string;
  disabled?: boolean;
}) {
  const t = useTranslations('components.ui.pagination');

  return (
    <PaginationLink
      aria-label={t('previous-page')}
      size="default"
      className={cn(
        'pl-2!',
        disabled && 'pointer-events-none opacity-50',
        className,
      )}
      {...props}
    >
      <CaretLeftIcon data-icon="inline-start" />
      <span className="hidden sm:block">{text}</span>
    </PaginationLink>
  );
}

function PaginationNext({
  className,
  text = '',
  disabled = false,
  ...props
}: React.ComponentProps<typeof PaginationLink> & {
  text?: string;
  disabled?: boolean;
}) {
  const t = useTranslations('components.ui.pagination');

  return (
    <PaginationLink
      aria-label={t('next-page')}
      size="default"
      className={cn(
        'pr-2!',
        disabled && 'pointer-events-none opacity-50',
        className,
      )}
      {...props}
    >
      <span className="hidden sm:block">{text}</span>
      <CaretRightIcon data-icon="inline-end" />
    </PaginationLink>
  );
}

function PaginationEllipsis({
  className,
  ...props
}: React.ComponentProps<'span'>) {
  const t = useTranslations('components.ui.pagination');

  return (
    <span
      aria-hidden
      data-slot="pagination-ellipsis"
      className={cn(
        "size-9 [&_svg:not([class*='size-'])]:size-4 flex items-center justify-center",
        className,
      )}
      {...props}
    >
      <DotsThreeIcon />
      <span className="sr-only">{t('more-pages')}</span>
    </span>
  );
}

export {
  Pagination,
  PaginationContent,
  PaginationEllipsis,
  PaginationItem,
  PaginationLink,
  PaginationNext,
  PaginationPrevious,
};
