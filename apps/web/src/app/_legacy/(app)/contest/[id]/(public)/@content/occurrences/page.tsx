import ContestOccurrencesTable from './_components/ContestOccurrencesTable';

interface ContestOccurrencesProps {
  params: Promise<{
    id: string;
  }>;
}

export default async function ContestOccurrencesPage({
  params,
}: ContestOccurrencesProps) {
  const { id } = await params;

  return <ContestOccurrencesTable contestId={id} />;
}
