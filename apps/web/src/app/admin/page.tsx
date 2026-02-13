'use client';

import { useRouter } from 'next/navigation';

import LoadingPage from '@/components/layout/loading/LoadingPage';
import { useSession } from '@/lib/auth/client';

import UnverifiedProviders from './_components/UnverifiedProviders';

export default function Admin() {
  const router = useRouter();

  const { data: session, isPending } = useSession();

  if (isPending) {
    return <LoadingPage />;
  }

  if (!session || session.user.role !== 'ADMIN') {
    router.push('/');
  }

  return (
    <div className="flex flex-col gap-4">
      <UnverifiedProviders />
    </div>
  );
}
