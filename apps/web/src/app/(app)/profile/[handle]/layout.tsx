import { Metadata } from 'next';

import { getProfileByHandle } from '@/api/__generated__/profile/profile';
import { TripleColumnLayout } from '@/components/layout/columns';

interface AppLayoutProps {
  children: React.ReactNode;
  params: Promise<{
    handle: string;
  }>;
}

export async function generateMetadata({
  params,
}: AppLayoutProps): Promise<Metadata> {
  const { handle } = await params;

  try {
    const { data: profile } = await getProfileByHandle(handle);

    return { title: profile.name };
  } catch {
    return {};
  }
}

export default function AppLayout({ children }: AppLayoutProps) {
  return (
    <TripleColumnLayout main={children} left={undefined} right={undefined} />
  );
}
