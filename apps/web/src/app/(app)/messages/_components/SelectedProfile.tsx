import Link from 'next/link';
import { useRouter } from 'next/navigation';
import { useEffect } from 'react';

import { Avatar, AvatarFallback } from '@/components/ui/avatar';
import { useGetProfile } from '@/features/profile/api/get-profile';

interface ProfileProps {
  to: string;
}

export default function SelectedProfile({ to }: ProfileProps) {
  const router = useRouter();
  const { data: profile, isError } = useGetProfile(to);

  useEffect(() => {
    if (isError) {
      router.push('/messages');
    }
  }, [isError, router]);

  return (
    <div className="flex items-center gap-4">
      <Link href={`/profile/${to}`}>
        <Avatar className="w-10 h-10">
          <AvatarFallback></AvatarFallback>
        </Avatar>
      </Link>

      <div>
        <h3 className="font-semibold">{profile?.name}</h3>
        <p className="text-xs font-light">{profile?.email}</p>
      </div>
    </div>
  );
}
