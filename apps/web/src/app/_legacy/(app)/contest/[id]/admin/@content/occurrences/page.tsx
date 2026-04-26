import CreateContestOccurrenceForm from './_components/CreateContestOccurrenceFormDialog';

interface ContestOccurrencesPageProps {
  params: Promise<{
    id: string;
  }>;
}

export default async function ContestOccurrencesPage({
  params,
}: ContestOccurrencesPageProps) {
  const { id } = await params;

  return <CreateContestOccurrenceForm contestId={id} />;
}
