import ProviderAdminPage from '@/features/provider/components/ProviderAdminPage';
import { ProviderType } from '@/types/provider';

interface ProjectProps {
  params: Promise<{
    id: string;
  }>;
}

export default async function ProjectAdmin({ params }: ProjectProps) {
  const { id } = await params;

  return <ProviderAdminPage id={id} providerType={ProviderType.PROJECT} />;
}
