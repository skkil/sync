'use client';

import Link from 'next/link';

import { useGetProjectByHandle } from '@/api/__generated__/project/project';
import { Avatar, AvatarFallback } from '@/components/ui/avatar';
import { Badge } from '@/components/ui/badge';
import { Button } from '@/components/ui/button';
import { isAuthenticated } from '@/lib/auth';
import { useSession } from '@/lib/auth/client';

interface ProjectHeaderProps {
  handle: string;
}

export default function ProjectHeader({ handle }: ProjectHeaderProps) {
  const { data: session } = useSession();
  const { data, isPending } = useGetProjectByHandle(handle);

  if (isPending) {
    return null;
  }

  if (!data) {
    return null;
  }

  const { data: project } = data;

  return (
    <div className="flex items-center justify-between">
      <div className="flex items-center gap-3">
        <Avatar className="w-12 h-12">
          <AvatarFallback></AvatarFallback>
        </Avatar>

        <div className="flex flex-col leading-4">
          <span className="text-2xl">{project.name}</span>
          <span>@{project.handle}</span>
        </div>
        <span>
          <Badge>{project.isPublic ? 'Public' : 'Private'}</Badge>
        </span>
      </div>

      <div className="flex items-center gap-2">
        {isAuthenticated(session) && <Button>Follow</Button>}
        {project.isMember && (
          <Button asChild>
            <Link href={`/project/${project.handle}/settings`}>Settings</Link>
          </Button>
        )}
      </div>
    </div>
  );
}
