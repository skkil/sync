import ProviderAdminPage from '@/features/provider/components/ProviderAdminPage';
import { ProviderType } from '@/types/provider';

interface CompanyProps {
  params: Promise<{
    id: string;
  }>;
}

export default async function CompanyAdmin({ params }: CompanyProps) {
  const { id } = await params;

  return <ProviderAdminPage id={id} providerType={ProviderType.COMPANY} />;
}
