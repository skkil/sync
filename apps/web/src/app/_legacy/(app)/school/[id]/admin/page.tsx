import ProviderAdminPage from '@/features/provider/components/ProviderAdminPage';
import { ProviderType } from '@/types/provider';

interface SchoolProps {
  params: Promise<{
    id: string;
  }>;
}

export default async function SchoolAdmin({ params }: SchoolProps) {
  const { id } = await params;

  return <ProviderAdminPage id={id} providerType={ProviderType.SCHOOL} />;
}
