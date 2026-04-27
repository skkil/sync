import Link from 'next/link';
import { useRouter } from 'next/navigation';
import { useEffect } from 'react';

import { useGetProfileByHandle } from '@/api/__generated__/profile/profile';
import { Avatar, AvatarFallback, AvatarImage } from '@/components/ui/avatar';

interface ProfileProps {
  to: string;
}

export default function SelectedProfile({ to }: ProfileProps) {
  const router = useRouter();
  const { data: profile, isError } = useGetProfileByHandle(to);

  useEffect(() => {
    if (isError) {
      router.push('/messages');
    }
  }, [isError, router]);

  return (
    <div className="flex items-center gap-4">
      <Link href={`/profile/${to}`}>
        <Avatar className="w-10 h-10">
          <AvatarImage src={profile?.data.profileImageUrl ?? undefined} />
          <AvatarFallback></AvatarFallback>
        </Avatar>
      </Link>

      <div>
        <h3 className="font-semibold">{profile?.data.name}</h3>
        <p className="text-xs font-light">{profile?.data.email}</p>
      </div>
    </div>
  );
}
