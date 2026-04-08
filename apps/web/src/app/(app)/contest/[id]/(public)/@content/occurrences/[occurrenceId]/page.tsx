import { ArrowLeftIcon } from '@phosphor-icons/react/ssr';
import { getTranslations } from 'next-intl/server';
import Link from 'next/link';

import { Button } from '@/components/ui/button';

import ContestOccurrenceOverview from './_components/ContestOccurrenceOverview';

interface ContestOccurrenceParams {
  params: Promise<{
    id: string;
    occurrenceId: string;
  }>;
}

export default async function ContestOccurrencePage({
  params,
}: ContestOccurrenceParams) {
  const { id, occurrenceId } = await params;
  const t = await getTranslations('pages.contest.occurrences.details');

  return (
    <div>
      <Link href={`/contest/${id}/occurrences?m=true`}>
        <Button variant="ghost" className="mb-3">
          <ArrowLeftIcon />
          {t('back-to-occurrences')}
        </Button>
      </Link>

      <ContestOccurrenceOverview contestId={id} occurrenceId={occurrenceId} />
    </div>
  );
}
