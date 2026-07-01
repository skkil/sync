import Link from 'next/link';

import { Button } from '@/components/ui/button';
import {
  Empty,
  EmptyContent,
  EmptyHeader,
  EmptyTitle,
} from '@/components/ui/empty';

interface NotFoundProps {
  title: string;
  description: string;
  backLabel: string;
  backHref?: string;
}

export function NotFound({
  title,
  description,
  backLabel,
  backHref = '/',
}: NotFoundProps) {
  return (
    <div className="flex w-full items-center justify-center">
      <Empty>
        <EmptyHeader>
          <EmptyTitle>{title}</EmptyTitle>
        </EmptyHeader>
        <EmptyContent>
          <p className="text-muted-foreground">{description}</p>
          <Button asChild variant="outline">
            <Link href={backHref}>{backLabel}</Link>
          </Button>
        </EmptyContent>
      </Empty>
    </div>
  );
}
