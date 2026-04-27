'use client';

import { useGetContestOccurrence } from '@/api/__generated__/contests/contests';

interface ContestOccurrenceOverviewProps {
  contestId: string;
  occurrenceId: string;
}

export default function ContestOccurrenceOverview({
  contestId,
  occurrenceId,
}: ContestOccurrenceOverviewProps) {
  const { data: contestOccurrence } = useGetContestOccurrence(
    contestId,
    occurrenceId,
  );

  return (
    <div>
      <h1 className="text-2xl font-bold mb-4">
        {contestOccurrence?.data?.title}
      </h1>
    </div>
  );
}
